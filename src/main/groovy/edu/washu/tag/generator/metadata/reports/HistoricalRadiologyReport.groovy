package edu.washu.tag.generator.metadata.reports

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.parser.PipeParser
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObrGeneratorHistorical
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObxGeneratorHistorical
import edu.washu.tag.generator.hl7.v2.segment.PidGenerator
import edu.washu.tag.generator.hl7.v2.segment.PidGeneratorHistorical
import edu.washu.tag.generator.hl7.v2.segment.Pv1Generator
import edu.washu.tag.generator.hl7.v2.segment.Pv1GeneratorHistorical
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.util.FileIOUtils
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.StringReplacements
import java.time.format.DateTimeFormatter

class HistoricalRadiologyReport extends CurrentRadiologyReport {

    private static final String BASE_REPORT_SKELETON = FileIOUtils.readResource('legacy_report_skeleton.txt')
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern('LLL dd uuuu hh:mma') // TODO: technically there are 2 formats used, not just this one
    private static final Parser cachedParser = cacheParser()

    @Override
    String getHl7Version() {
        '2.4'
    }

    @Override
    protected PidGenerator getPidGenerator() {
        new PidGeneratorHistorical()
    }

    @Override
    protected Pv1Generator getPv1Generator() {
        new Pv1GeneratorHistorical()
    }

    @Override
    protected ObrGenerator getObrGenerator() {
        new ObrGeneratorHistorical()
    }

    @Override
    protected boolean includeOrc() {
        false
    }

    @Override
    protected boolean includeZpfAndZds() {
        false
    }

    @Override
    void postProcess() {
        super.postProcess()
        setParser(cachedParser)
    }

    @Override
    protected void addObx(ORU_R01 radReport) {
        final String accessionNumber = fillerOrderNumber.getEi1_EntityIdentifier().value
        final List<ObxGenerator> obxGenerators = []

        final String observationId = RandomGenUtils.randomIdStr()

        final Person principalInterpreter = getEffectivePrincipalInterpreter()
        obxGenerators << new ObxGeneratorHistorical(
                "${principalInterpreter.formatFirstLast().toUpperCase()}, M.D.~~~~********${orcStatus.randomizeTitle()}********~~~ "
        ).setId('1')

        final CodedTriplet procedureCode = ProcedureCode.lookup(study.procedureCodeId).codedTriplet
        obxGenerators.add(
                new ObxGeneratorHistorical(
                        BASE_REPORT_SKELETON
                                .replace(StringReplacements.ACCESSION_NUMBER_PLACEHOLDER, accessionNumber)
                                .replace(StringReplacements.DATE_TIME_PLACEHOLDER, DATE_TIME_FORMATTER.format(reportDateTime))
                                .replace(StringReplacements.PROCEDURE, "${procedureCode.codeValue.replace('\\D', '')} ${procedureCode.codeMeaning}")
                                .replace(StringReplacements.EXAMINATION_PLACEHOLDER, generatedReport.examination)
                                .replace(StringReplacements.FINDINGS_PLACEHOLDER, generatedReport.findings)
                                .replace(StringReplacements.IMPRESSIONS_PLACEHOLDER, generatedReport.impressions)
                ).setId('2')
        )

        obxGenerators << generateDictation()

        obxGenerators << new ObxGeneratorHistorical(accessionNumber, false).observationSubId('4')

        obxGenerators.eachWithIndex { obxGenerator, i ->
            obxGenerator
                    .observationId(observationId)
                    .generateSegment(this, radReport.PATIENT_RESULT.ORDER_OBSERVATION.getOBSERVATION(i).OBX)
        }
    }

    private ObxGenerator generateDictation() {
        final Person interpreter = getEffectivePrincipalInterpreter()
        final Person reviewer = assistantInterpreters[0]

        final String endingSection = orcStatus == ReportStatus.FINAL ?
            "This document has been electronically signed by: ${interpreter.formatFirstLast()}, M.D. on ${DATE_TIME_FORMATTER.format(reportDateTime)}" :
            "Pending review by: ${reviewer.formatFirstLast()}, M.D."

        new ObxGeneratorHistorical(
            "Requested By: ${orderingProvider.formatLastFirstMiddle(false)} M.D."
                + "~~Dictated By: ${interpreter.formatFirstLast()}, M.D. on "
                + "${DATE_TIME_FORMATTER.format(reportDateTime)}~~${endingSection}~ ~~~ ~~~ "
        ).setId('3')
    }

    private static Parser cacheParser() {
        final Parser pipeParser = new PipeParser()
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/VISIT/PV1-8-3')
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/VISIT/PV1-9-3')
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/ORDER_OBSERVATION/OBR-33-3')
        pipeParser
    }

}
