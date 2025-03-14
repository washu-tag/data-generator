package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.Hl7LogFile
import edu.washu.tag.generator.Hl7Logger
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class FormHl7LogActivityImpl implements FormHl7LogActivity {

    private static final Logger logger = Workflow.getLogger(FormHl7LogActivityImpl)

    @Override
    Map<String, List<Hl7LogFile>> identifyLogFiles(File baseDir) {
        logger.info("Identifying HL7-ish log files to generate from ${baseDir}")
        new Hl7Logger().identifyHl7LogFiles(baseDir).groupBy { it.year }
    }

    @Override
    void formLogFile(List<Hl7LogFile> hl7LogFiles) {
        hl7LogFiles.each { hl7LogFile ->
            logger.info("Preparing HL7-ish log file ${hl7LogFile.asFile.name}")
            new Hl7Logger().writeToHl7ishLogFile(hl7LogFile)
            logger.info("Successfully wrote ${hl7LogFile.asFile.name}")
        }
    }

}
