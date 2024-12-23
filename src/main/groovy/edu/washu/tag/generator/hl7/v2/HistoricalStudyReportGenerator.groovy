package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.reports.HistoricalRadiologyReport

class HistoricalStudyReportGenerator extends CurrentStudyReportGenerator {

    @Override
    protected void chooseInterpreters(RadiologyReport radReport, Institution institution, MessageRequirements messageRequirements) {
        final List<Person> allInterpreters = NameCache.selectPhysicians(
                institution,
                messageRequirements.numAsstInterpreters + 1
        )
        radReport.setPrincipalInterpreter(allInterpreters[0])
        radReport.setAssistantInterpreters(allInterpreters[1 .. -1])
    }

    @Override
    protected RadiologyReport initReport() {
        return new HistoricalRadiologyReport()
    }

}
