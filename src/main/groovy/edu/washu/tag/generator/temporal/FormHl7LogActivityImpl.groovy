package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.Hl7LogFile
import edu.washu.tag.generator.Hl7Logger
import io.temporal.workflow.Workflow
import org.slf4j.Logger

class FormHl7LogActivityImpl implements FormHl7LogActivity {

    private static final Logger logger = Workflow.getLogger(FormHl7LogActivityImpl)

    @Override
    void formLogFile(Hl7LogFile hl7LogFile) {
        logger.info("Preparing HL7-ish log file ${hl7LogFile.asFile.name}")
        new Hl7Logger().writeToHl7ishLogFile(hl7LogFile)
        logger.info("Successfully wrote ${hl7LogFile.asFile.name}")
    }

}
