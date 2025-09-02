package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study

abstract class StudyReportGenerator {

    abstract RadiologyReport generateReportFrom(Patient patient, Study study, MessageRequirements messageRequirements, GeneratedReport generatedReport)

    abstract RadiologyReport initReport()

    abstract Boolean checkReportCompatibility(GeneratedReport generatedReport)

}
