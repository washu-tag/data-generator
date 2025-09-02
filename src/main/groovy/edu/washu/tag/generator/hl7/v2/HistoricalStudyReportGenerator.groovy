package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObxGeneratorHistorical
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.reports.HistoricalRadiologyReport

import java.time.format.DateTimeFormatter

class HistoricalStudyReportGenerator extends CurrentStudyReportGenerator {

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern('LLL dd uuuu hh:mma') // TODO: technically there are 2 formats used, not just this one

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
    RadiologyReport initReport() {
        return new HistoricalRadiologyReport()
    }

    static List<ObxGenerator> generateObx(RadiologyReport radiologyReport, String mainReportText) {
        final String accessionNumber = radiologyReport.fillerOrderNumber.getEi1_EntityIdentifier().value
        final List<ObxGenerator> obxGenerators = []

        final Person principalInterpreter = radiologyReport.getEffectivePrincipalInterpreter()
        obxGenerators << new ObxGeneratorHistorical(
            "${principalInterpreter.formatFirstLast().toUpperCase()}, M.D.~~~~********${radiologyReport.orcStatus.randomizeTitle()}********~~~ "
        ).setId('1')

        obxGenerators.add(
            new ObxGeneratorHistorical(mainReportText).setId('2')
        )

        obxGenerators << generateDictation(radiologyReport)

        obxGenerators << new ObxGeneratorHistorical(accessionNumber, false).observationSubId('4')

        obxGenerators
    }

    private static ObxGenerator generateDictation(RadiologyReport radiologyReport) {
        final Person interpreter = radiologyReport.getEffectivePrincipalInterpreter()
        final Person reviewer = radiologyReport.assistantInterpreters[0]

        final String endingSection = radiologyReport.orcStatus == ReportStatus.FINAL ?
            "This document has been electronically signed by: ${interpreter.formatFirstLast()}, M.D. on ${DATE_TIME_FORMATTER.format(radiologyReport.reportDateTime)}" :
            "Pending review by: ${reviewer.formatFirstLast()}, M.D."

        new ObxGeneratorHistorical(
            "Requested By: ${radiologyReport.orderingProvider.formatLastFirstMiddle(false)} M.D."
                + "~~Dictated By: ${interpreter.formatFirstLast()}, M.D. on "
                + "${DATE_TIME_FORMATTER.format(radiologyReport.reportDateTime)}~~${endingSection}~ ~~~ ~~~ "
        ).setId('3')
    }

}
