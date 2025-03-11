package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.Hl7LogFile
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.activity.FormHl7LogActivity
import edu.washu.tag.generator.temporal.model.GenerateBatchInput
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Async
import io.temporal.workflow.ChildWorkflowOptions
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import org.slf4j.Logger

import java.time.Duration

@WorkflowImpl(taskQueues = TemporalApplication.PARENT_QUEUE)
class CombineHl7WorkflowImpl implements CombineHl7Workflow {

    private final Logger logger = Workflow.getLogger(CombineHl7WorkflowImpl)
    private final FormHl7LogActivity hl7LogActivity =
        Workflow.newActivityStub(
            FormHl7LogActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(10))
                .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumInterval(Duration.ofSeconds(1))
                    .setMaximumAttempts(3)
                    .build())
                .build()
        )

    @Override
    void combineLogs(File hl7BaseDir) {
        final List<Hl7LogFile> hl7LogFiles = hl7LogActivity.identifyLogFiles(hl7BaseDir)
        logger.info("Attempting to create combined ${hl7LogFiles.size()} HL7-ish log files")
        Promise.allOf(hl7LogFiles.collect { hl7LogFile ->
            Async.function(hl7LogActivity.&formLogFile, hl7LogFile)
        }).get()
    }

}
