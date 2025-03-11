package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.Hl7LogFile
import edu.washu.tag.generator.Hl7Logger
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@ActivityImpl(taskQueues = TemporalApplication.PARENT_QUEUE)
class FormHl7LogActivityImpl implements FormHl7LogActivity {

    private static final Logger logger = Workflow.getLogger(FormHl7LogActivityImpl)

    @Override
    void formLogFiles(List<Hl7LogFile> hl7LogFiles) {
        hl7LogFiles.each { hl7LogFile ->
            logger.info("Preparing HL7-ish log file ${hl7LogFile.asFile.name}")
            new Hl7Logger().writeToHl7ishLogFile(hl7LogFile)
            logger.info("Successfully wrote ${hl7LogFile.asFile.name}")
        }
    }

}
