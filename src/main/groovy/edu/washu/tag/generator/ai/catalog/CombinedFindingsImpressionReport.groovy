package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.*
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.HistoricalReportStructurer
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObxManager
import edu.washu.tag.generator.metadata.*
import edu.washu.tag.generator.metadata.protocols.MammogramFourView
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.StringReplacements
import edu.washu.tag.util.FileIOUtils

class CombinedFindingsImpressionReport extends GeneratedReport<CombinedFindingsImpressionReport> implements
    WithExamination,
    WithHistory {

    @JsonPropertyDescription('Detailed and lengthy narrative description of images including professional conclusion of radiologist')
    String impression

    @JsonIgnore
    ImpressionFormat impressionFormat = RandomGenUtils.randomEnumValue(ImpressionFormat)

    private static final String BASE_REPORT_SKELETON_HISTORICAL = FileIOUtils.readResource('legacy_report_skeleton_combined.txt')

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        final String comparison = studyRep.compareTo == null ? 'There is no previous known comparison, so the comparison property should contain only a brief mention of no comparison available. ' : ''
        "${comparison}Be brief in the examination section. The impression section should be significantly detailed."
    }

    @Override
    String writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        impressionFormat = ImpressionFormat.STANDALONE
        final String accessionNumber = radiologyReport.fillerOrderNumber.getEi1_EntityIdentifier().value
        final CodedTriplet procedureCode = ProcedureCode.lookup(radiologyReport.study.procedureCodeId).codedTriplet
        final String procedure = "${procedureCode.codeValue.replace('\\D', '')} ${procedureCode.codeMeaning}"
        BASE_REPORT_SKELETON_HISTORICAL
            .replace(StringReplacements.ACCESSION_NUMBER_PLACEHOLDER, accessionNumber)
            .replace(StringReplacements.DATE_TIME_PLACEHOLDER, HistoricalReportStructurer.DATE_TIME_FORMATTER.format(radiologyReport.reportDateTime))
            .replace(StringReplacements.PROCEDURE, procedure)
            .replace(StringReplacements.EXAMINATION_PLACEHOLDER, examination)
            .replace(StringReplacements.HISTORY_PLACEHOLDER, history)
            .replace(StringReplacements.IMPRESSION_PLACEHOLDER, impressionFormat.serializeImpression(impression))
    }

    @Override
    ObxManager writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ObxManager obxGenerators = new ObxManager(
            [
                "EXAMINATION: ${examination}\n",
                "HISTORY: ${history}\n"
            ].collect { ObxGenerator.forGeneralDescription(it) }
        )

        obxGenerators.add(
            ObxGenerator.forImpression(
                impressionFormat.serializeImpression(impression)
            )
        )

        final Person interpreter = radiologyReport.getEffectivePrincipalInterpreter()
        obxGenerators.add(
            ObxGenerator.forImpression(
                "Dictated by: ${interpreter.givenNameAlphabetic} ${interpreter.familyNameAlphabetic} Interpreter, M.D."
            )
        )

        obxGenerators
    }

}
