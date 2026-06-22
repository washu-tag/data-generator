package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.temporal.activity.BatchHandlerActivity
import edu.washu.tag.generator.temporal.activity.EarlySetupHandlerActivity
import edu.washu.tag.generator.temporal.model.BatchHandlerActivityInput
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

import java.time.Duration

@WorkflowImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class GenerateDatasetWorkflowImpl implements GenerateDatasetWorkflow {

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

    private final BatchHandlerActivity batchHandlerActivity =
        Workflow.newActivityStub(
            BatchHandlerActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofHours(24))
                .setHeartbeatTimeout(Duration.ofMinutes(15))
                .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumInterval(Duration.ofSeconds(1))
                    .setMaximumAttempts(3)
                    .build())
                .build()
        )

    @Override
    void generateDataset(GenerateDatasetInput input) {
        final WorkflowInfo workflowInfo = Workflow.getInfo()
        final String workflowLoggingInfo = "[workflowId: ${workflowInfo.workflowId}]"
        logger.info("${workflowLoggingInfo} Beginning workflow ${this.class.simpleName}")

        if (input.outputDir == null) {
            input.outputDir = String.valueOf(Workflow.currentTimeMillis())
        }

        final File nameCache = earlySetupActivity.initGenerationCache(input.specificationParametersPath, input.outputFullPath())
        final IdOffsets idOffsets = new IdOffsets()

        final List<BatchRequest> batches = earlySetupActivity.resolveBatches(
            input.specificationParametersPath,
            input.outputFullPath(),
            input.patientsPerFullBatch
        )
        logger.info("${workflowLoggingInfo} fanning out ${batches.size()} standalone batches")

        // Each batch is an independent activity. Effective concurrency is (number of workers ×
        // maxConcurrentActivityExecutionSize), not anything encoded here - excess batches simply queue on the task
        // queue until a worker slot frees. NOTE: every batch is scheduled up front, so each adds events to this
        // workflow's history. Temporal warns near 10k events and hard-caps ~50k, leaving comfortable headroom to
        // ~1-2k batches per run; beyond that, window the fan-out with continueAsNew (the window would be a history
        // checkpoint only, not a concurrency knob).
        Promise.allOf(batches.collect { batchRequest ->
            Async.function(
                batchHandlerActivity::formAndWriteBatch,
                new BatchHandlerActivityInput(input, nameCache, idOffsets, input.concurrentExecution, batchRequest))
        }).get()

        logger.info("${workflowLoggingInfo} all batches have been written")
        BatchProcessor.initDirs(input.outputFullPath())

        // Output combined HL7(-ish) log files now that all results are prepared
        Async.function(
            Workflow.newChildWorkflowStub(
                CombineHl7Workflow,
                ChildWorkflowOptions.newBuilder()
                    .setTaskQueue(TemporalApplication.TASK_QUEUE)
                    .setWorkflowId("${workflowInfo.workflowId}/combine-hl7")
                    .build()
            )::combineLogs,
            BatchProcessor.hl7Output
        ).get()
    }

}
