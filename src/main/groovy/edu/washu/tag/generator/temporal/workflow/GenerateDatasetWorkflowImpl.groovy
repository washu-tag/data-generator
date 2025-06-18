package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.temporal.activity.EarlySetupHandlerActivity
import edu.washu.tag.generator.temporal.model.BatchChunk
import edu.washu.tag.generator.temporal.model.GenerateBatchesInput
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

    @Override
    void generateDataset(GenerateDatasetInput input) {
        final WorkflowInfo workflowInfo = Workflow.getInfo()
        final String workflowLoggingInfo = "[workflowId: ${workflowInfo.workflowId}]"
        logger.info("${workflowLoggingInfo} Beginning workflow ${this.class.simpleName}")

        final File nameCache = earlySetupActivity.initNameCache()
        final IdOffsets idOffsets = new IdOffsets()

        final List<BatchChunk> batchRequests = earlySetupActivity.chunkBatches(input.specificationParametersPath, input.concurrentExecution, input.outputDir)

        // Launch child workflow where each fulfills several batches in sequence
        Promise.allOf(batchRequests.collect { batchChunk ->
            Async.function(
                Workflow.newChildWorkflowStub(
                    GenerateBatchWorkflow,
                    ChildWorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalApplication.TASK_QUEUE)
                        .setWorkflowId("${workflowInfo.workflowId}/${batchChunk.workflowSubid()}")
                        .setWorkflowTaskTimeout(Duration.ofSeconds(30))
                        .build()
                )::generateBatches,
                new GenerateBatchesInput(input, nameCache, idOffsets, batchChunk, input.outputDir))
        }).get()

        logger.info("${workflowLoggingInfo} all batches have been written")

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
