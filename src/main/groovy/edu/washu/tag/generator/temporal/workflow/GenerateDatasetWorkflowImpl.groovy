package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.Batcher
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.OutputManager
import edu.washu.tag.generator.temporal.activity.BatchHandlerActivity
import edu.washu.tag.generator.temporal.activity.EarlySetupHandlerActivity
import edu.washu.tag.generator.temporal.model.BatchHandlerActivityInput
import edu.washu.tag.generator.temporal.model.BatchedRequestWithContinuation
import edu.washu.tag.generator.temporal.model.ContinueGenerationWorkflowInput
import edu.washu.tag.generator.temporal.model.ExtendSpecWorkflowInput
import edu.washu.tag.generator.temporal.model.GenerateDatasetInput
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Async
import io.temporal.workflow.ChildWorkflowOptions
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInfo
import org.slf4j.Logger

import java.nio.file.Paths
import java.time.Duration

@WorkflowImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class GenerateDatasetWorkflowImpl implements GenerateDatasetWorkflow, ExtendSpecWorkflow, ContinueGenerationWorkflow {

    private static final Logger logger = Workflow.getLogger(GenerateDatasetWorkflowImpl)

    private final EarlySetupHandlerActivity earlySetupActivity =
        Workflow.newActivityStub(
            EarlySetupHandlerActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(15))
                .setTaskQueue(TemporalApplication.TASK_QUEUE)
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setMaximumAttempts(1)
                        .build()
                ).build()
        )

    @Override
    void generateDataset(GenerateDatasetInput input) {
        logInfoWithWorkflowId("Beginning workflow ${this.class.simpleName}, generateDataset")

        if (input.outputDir == null) {
            input.outputDir = String.valueOf(Workflow.currentTimeMillis())
        }

        final OutputManager outputManager = new OutputManager(input.outputFullPath())
        outputManager.ensureNonempty()
        outputManager.copySpecificationParameters(input.specificationParametersPath)
        earlySetupActivity.initGenerationCache(input.specificationParametersPath, input.outputFullPath())
        final IdOffsets fixedOffsets = new IdOffsets()
        outputManager.writeFixedOffsets(fixedOffsets)

        fulfillDataset(
            input,
            earlySetupActivity.resolveBatches(
                input.specificationParametersPath,
                input.outputFullPath(),
                input.patientsPerFullBatch
            ),
            fixedOffsets
        )
    }

    @Override
    void continueGeneration(ContinueGenerationWorkflowInput continueGenerationWorkflowInput) {
        logInfoWithWorkflowId("Beginning workflow ${this.class.simpleName}, continueGeneration")

        final GenerateDatasetInput datasetInput = new GenerateDatasetInput(
            specificationParametersPath: continueGenerationWorkflowInput.newSpecificationPath,
            writeDicom: continueGenerationWorkflowInput.writeDicom,
            writeHl7: continueGenerationWorkflowInput.writeHl7,
            outputDir: continueGenerationWorkflowInput.previousDataset,
            patientsPerFullBatch: continueGenerationWorkflowInput.patientsPerFullBatch
        )

        fulfillDataset(
            datasetInput,
            earlySetupActivity.resolveBatchesForContinuation(continueGenerationWorkflowInput),
            new OutputManager(datasetInput.outputFullPath()).readFixedOffsets()
        )
    }

    @Override
    void extendSpec(ExtendSpecWorkflowInput extendSpecWorkflowInput) {
        logInfoWithWorkflowId("Beginning workflow ${this.class.simpleName}, extendSpec")

        final GenerateDatasetInput datasetInput = new GenerateDatasetInput(
            writeDicom: extendSpecWorkflowInput.writeDicom,
            writeHl7: extendSpecWorkflowInput.writeHl7,
            outputDir: extendSpecWorkflowInput.previousDataset,
            patientsPerFullBatch: extendSpecWorkflowInput.patientsPerFullBatch,
        )
        final OutputManager outputManager = new OutputManager(datasetInput.outputFullPath())
        final BatchedRequestWithContinuation batches = earlySetupActivity.resolveBatchesForExtendedSpec(extendSpecWorkflowInput)

        datasetInput.setSpecificationParametersPath(outputManager.getContinuationSpecificationParametersPath(batches.continuation))

        fulfillDataset(
            datasetInput,
            batches,
            outputManager.readFixedOffsets()
        )
    }

    private void fulfillDataset(GenerateDatasetInput input, BatchedRequestWithContinuation batchesWithContinuation, IdOffsets idOffsets) {
        final List<BatchRequest> batches = batchesWithContinuation.batches
        logInfoWithWorkflowId("fanning out ${batches.size()} standalone batches")

        // Each batch is an independent activity. Effective concurrency is (number of workers ×
        // maxConcurrentActivityExecutionSize), not anything encoded here - excess batches simply queue on the task
        // queue until a worker slot frees. NOTE: every batch is scheduled up front, so each adds events to this
        // workflow's history. Temporal warns near 10k events and hard-caps ~50k, leaving comfortable headroom to
        // ~1-2k batches per run; beyond that, window the fan-out with continueAsNew (the window would be a history
        // checkpoint only, not a concurrency knob).
        Promise.allOf(batches.collect { batchRequest ->
            final BatchHandlerActivity batchHandlerActivity =
                Workflow.newActivityStub(
                    BatchHandlerActivity,
                    ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofHours(24))
                        .setHeartbeatTimeout(Duration.ofMinutes(15))
                        .setSummary(batchRequest.temporalSummary)
                        .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumInterval(Duration.ofSeconds(1))
                            .setMaximumAttempts(3)
                            .build())
                        .build()
                )

            Async.function(
                batchHandlerActivity::formAndWriteBatch,
                new BatchHandlerActivityInput(input, idOffsets, batchRequest))
        }).get()

        logInfoWithWorkflowId("all batches have been written")
        BatchProcessor.initDirs(input.outputFullPath())

        // Output combined HL7(-ish) log files now that all results are prepared
        Async.function(
            Workflow.newChildWorkflowStub(
                CombineHl7Workflow,
                ChildWorkflowOptions.newBuilder()
                    .setTaskQueue(TemporalApplication.TASK_QUEUE)
                    .setWorkflowId("${Workflow.getInfo().workflowId}/combine-hl7")
                    .build()
            )::combineLogs,
            BatchProcessor.hl7Output
        ).get()

        new OutputManager(input.outputFullPath()).writeContinuationToOutput(batchesWithContinuation.continuation)
    }

    private void logInfoWithWorkflowId(String logged) {
        logger.info("[workflowId: ${Workflow.getInfo().workflowId}] ${logged}")
    }

}
