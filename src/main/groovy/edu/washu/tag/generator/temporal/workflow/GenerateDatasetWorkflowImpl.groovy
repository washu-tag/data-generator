package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.Hl7Logger
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.temporal.activity.EarlySetupHandlerActivity
import edu.washu.tag.generator.temporal.activity.FormHl7LogActivity
import edu.washu.tag.generator.temporal.model.GenerateBatchInput
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

@WorkflowImpl(taskQueues = TemporalApplication.PARENT_QUEUE)
class GenerateDatasetWorkflowImpl implements GenerateDatasetWorkflow {

    private static final Logger logger = Workflow.getLogger(GenerateDatasetWorkflowImpl)

    private final EarlySetupHandlerActivity earlySetupActivity =
        Workflow.newActivityStub(
            EarlySetupHandlerActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(15))
                .setTaskQueue(TemporalApplication.PARENT_QUEUE)
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setMaximumAttempts(1)
                        .build()
                ).build()
        )

    private final FormHl7LogActivity formHl7LogActivity =
        Workflow.newActivityStub(
            FormHl7LogActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(15))
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

        final NameCache nameCache = earlySetupActivity.initNameCache()
        final IdOffsets idOffsets = new IdOffsets()

        final List<BatchRequest> batchRequests = earlySetupActivity.chunkBatches(input.specificationParametersPath)

        // Launch child workflow for each batch to fulfill
        Promise.allOf(batchRequests.collect { batchRequest ->
            Async.function(
                Workflow.newChildWorkflowStub(
                    GenerateBatchWorkflow,
                    ChildWorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalApplication.CHILD_QUEUE)
                        .setWorkflowId("${workflowInfo.workflowId}/batch-${batchRequest.id}")
                        .build()
                ).&generateBatch,
                new GenerateBatchInput(input.specificationParametersPath, nameCache, idOffsets, batchRequest))
        }).get()

        logger.info("${workflowLoggingInfo} all batches have been written")

        // Output combined HL7(-ish) log files now that all results are prepared
        Async.function(
            formHl7LogActivity.&formLogFiles,
            new Hl7Logger().identifyHl7LogFiles(BatchProcessor.hl7Output)
        ).get()
    }

}
