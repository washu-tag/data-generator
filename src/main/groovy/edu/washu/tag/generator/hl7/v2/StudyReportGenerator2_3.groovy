package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.reports.RadiologyReport2_3

class StudyReportGenerator2_3 extends CurrentStudyReportGenerator {

    @Override
    protected void chooseInterpreters(RadiologyReport radReport, Institution institution, MessageRequirements messageRequirements) {
        chooseInterpretersWithPrimary(radReport, institution, messageRequirements)
    }

    @Override
    RadiologyReport initReport() {
        return new RadiologyReport2_3()
    }

    @Override
    protected void setOrderPlacerNumber(RadiologyReport radReport) {
        super.setOrderPlacerNumber(radReport) // TODO: this can be empty
        radReport.setPlacerOrderNumberNamespace(null) // TODO: this doesn't have to be empty
    }

}
