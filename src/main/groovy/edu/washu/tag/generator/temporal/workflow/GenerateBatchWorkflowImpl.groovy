package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.activity.BatchHandlerActivity
import edu.washu.tag.generator.temporal.model.GenerateBatchInput
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Workflow

import java.time.Duration

@WorkflowImpl(taskQueues = TemporalApplication.CHILD_QUEUE)
class GenerateBatchWorkflowImpl implements GenerateBatchWorkflow {

    private final BatchHandlerActivity batchHandlerActivity =
        Workflow.newActivityStub(
            BatchHandlerActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(30))
                .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumInterval(Duration.ofSeconds(1))
                    .setMaximumAttempts(3)
                    .build())
                .build()
        )

    @Override
    void generateBatch(GenerateBatchInput generateBatchInput) {
        batchHandlerActivity.formAndWriteBatch(generateBatchInput)
    }

}
