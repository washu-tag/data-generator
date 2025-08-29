package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.activity.BatchHandlerActivity
import edu.washu.tag.generator.temporal.model.BatchHandlerActivityInput
import edu.washu.tag.generator.temporal.model.GenerateBatchesInput
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Workflow

import java.time.Duration

@WorkflowImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class GenerateBatchesWorkflowImpl implements GenerateBatchWorkflow {

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
    void generateBatches(GenerateBatchesInput generateBatchInput) {
        BatchProcessor.initDirs(generateBatchInput.outputDir)
        generateBatchInput.batchChunk.resolveToBatches().each { batchRequest ->
            batchHandlerActivity.formAndWriteBatch(
                new BatchHandlerActivityInput(
                    generateBatchInput.datasetInput,
                    generateBatchInput.nameCachePath,
                    generateBatchInput.idOffsets,
                    batchRequest
                )
            ) // synchronous to process a whole batch before moving on
        }
    }

}
