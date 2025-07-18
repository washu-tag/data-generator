package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.Hl7LogFile
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.activity.FormHl7LogActivity
import edu.washu.tag.generator.temporal.model.FormHl7LogFileInput
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Async
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import org.slf4j.Logger

import java.time.Duration

@WorkflowImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class CombineHl7WorkflowImpl implements CombineHl7Workflow {

    private final Logger logger = Workflow.getLogger(CombineHl7WorkflowImpl)
    private final FormHl7LogActivity hl7LogActivity =
        Workflow.newActivityStub(
            FormHl7LogActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofHours(4))
                .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumInterval(Duration.ofSeconds(1))
                    .setMaximumAttempts(3)
                    .build())
                .build()
        )

    @Override
    void combineLogs(File hl7BaseDir) {
        final Map<String, List<Hl7LogFile>> hl7LogFileGroup = hl7LogActivity.identifyLogFiles(hl7BaseDir)
        logger.info("Attempting to create combined ${hl7LogFileGroup.values()*.size().sum()} HL7-ish log files")
        Promise.allOf(hl7LogFileGroup.collect { hl7LogFiles ->
            Async.procedure(hl7LogActivity::formLogFile, new FormHl7LogFileInput(hl7LogFiles: hl7LogFiles.value, outputDir: hl7BaseDir.toPath().parent.toString()))
        }).get()
    }

}
