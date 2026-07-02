package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchRequest
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

        // All filesystem work and offset creation happens inside the activity so the workflow stays deterministic
        // and side-effect free. setupNewDataset validates the output dir is empty, copies the spec, writes the
        // generation cache, and creates + persists the fixed ID offsets that every batch reads back from disk.
        earlySetupActivity.setupNewDataset(input)

        fulfillDataset(
            input,
            earlySetupActivity.resolveBatches(
                input.specificationParametersPath,
                input.outputFullPath(),
                input.patientsPerFullBatch
            )
        )
    }

    @Override
    void continueGeneration(ContinueGenerationWorkflowInput continueGenerationWorkflowInput) {
        logInfoWithWorkflowId("Beginning workflow ${this.class.simpleName}, continueGeneration")

        final GenerateDatasetInput datasetInput = new GenerateDatasetInput(
            writeDicom: continueGenerationWorkflowInput.writeDicom,
            writeHl7: continueGenerationWorkflowInput.writeHl7,
            outputDir: continueGenerationWorkflowInput.previousDataset,
            patientsPerFullBatch: continueGenerationWorkflowInput.patientsPerFullBatch
        )
        // resolveBatchesForContinuation archives the new spec as this run's continuation spec; point the batch
        // handler at that archived copy rather than the caller-supplied path (which may not be reachable from the
        // worker fulfilling a batch).
        final BatchedRequestWithContinuation batches = earlySetupActivity.resolveBatchesForContinuation(continueGenerationWorkflowInput)
        datasetInput.setSpecificationParametersPath(
            new OutputManager(datasetInput.outputFullPath()).getContinuationSpecificationParametersPath(batches.continuation)
        )

        fulfillDataset(datasetInput, batches)
    }

    @Override
    void extendSpec(ExtendSpecWorkflowInput extendSpecWorkflowInput) {
        logInfoWithWorkflowId("Beginning workflow ${this.class.simpleName}, extendSpec")

        final GenerateDatasetInput datasetInput = new GenerateDatasetInput(
            writeDicom: extendSpecWorkflowInput.writeDicom,
            writeHl7: extendSpecWorkflowInput.writeHl7,
            outputDir: extendSpecWorkflowInput.previousDataset,
            patientsPerFullBatch: extendSpecWorkflowInput.patientsPerFullBatch
        )
        final BatchedRequestWithContinuation batches = earlySetupActivity.resolveBatchesForExtendedSpec(extendSpecWorkflowInput)
        datasetInput.setSpecificationParametersPath(
            new OutputManager(datasetInput.outputFullPath()).getContinuationSpecificationParametersPath(batches.continuation)
        )

        fulfillDataset(datasetInput, batches)
    }

    private void fulfillDataset(GenerateDatasetInput input, BatchedRequestWithContinuation batchesWithContinuation) {
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
                new BatchHandlerActivityInput(input, batchRequest))
        }).get()

        logInfoWithWorkflowId("all batches have been written")

        // Output combined HL7(-ish) log files now that all results are prepared.
        Async.function(
            Workflow.newChildWorkflowStub(
                CombineHl7Workflow,
                ChildWorkflowOptions.newBuilder()
                    .setTaskQueue(TemporalApplication.TASK_QUEUE)
                    .setWorkflowId("${Workflow.getInfo().workflowId}/combine-hl7")
                    .build()
            )::combineLogs,
            Paths.get(input.outputFullPath(), 'hl7').toFile()
        ).get()

        // Advance the continuation cursor only after the entire run has succeeded. If the run fails before this,
        // the latest cursor stays put, so a retry reuses the same offsets/batch ids and deterministically
        // regenerates (each batch wipes its own output dir first), rather than skipping the range and orphaning
        // the partial data.
        earlySetupActivity.persistContinuation(input.outputFullPath(), batchesWithContinuation.continuation)
    }

    private void logInfoWithWorkflowId(String logged) {
        logger.info("[workflowId: ${Workflow.getInfo().workflowId}] ${logged}")
    }

}
