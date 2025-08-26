package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.WithComparison
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.ai.catalog.attribute.WithHistory
import edu.washu.tag.generator.ai.catalog.attribute.WithImpression
import edu.washu.tag.generator.ai.catalog.attribute.WithTechnique
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.HistoricalReportStructurer
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObxManager
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.protocols.MammogramFourView
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.StringReplacements
import edu.washu.tag.util.FileIOUtils

class SplitFindingsReport extends GeneratedReport<SplitFindingsReport> implements
    WithExamination,
    WithHistory,
    WithComparison,
    WithTechnique,
    WithImpression {

    @JsonPropertyDescription('Detailed and lengthy narrative description of images by body part')
    List<String> findings

    @JsonIgnore
    Set<String> expectedBodyParts = []

    @JsonIgnore
    FindingsFormat findingsFormat = RandomGenUtils.randomEnumValue(FindingsFormat)

    private static final String BASE_REPORT_SKELETON_HISTORICAL = FileIOUtils.readResource('legacy_split_report_skeleton.txt')

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        if (study.simpleDescription == MammogramFourView.SIMPLE_DESC) {
            expectedBodyParts = ['LEFT BREAST', 'RIGHT BREAST']
        } else {
            expectedBodyParts = [(study.bodyPartExamined.dicomRepresentation)] // TODO: improve all of this
        }
        final String comparison = studyRep.compareTo == null ? 'There is no previous known comparison, so the comparison property should contain only a brief mention of no comparison available. ' : ''
        "${comparison}Be brief in the examination section. The findings and impressions should be significantly detailed. When generating the findings section of the report, each string should begin with a body part, followed by a colon, and followed by the findings. The exact body parts that should be included are: ${expectedBodyParts}. The findings array must contain exactly ${expectedBodyParts.size()} items, one per provided body part."
    }

    @Override
    void preserveState(SplitFindingsReport destination) {
        destination.setExpectedBodyParts(expectedBodyParts)
    }

    @Override
    Boolean checkApplicability(Study study) {
        study.bodyPartExamined != null
    }

    @Override
    Boolean validateReport() {
        final Set<String> inferredBodyParts = findings.collect {
            it.split(':')[0].toUpperCase()
        }
        inferredBodyParts == expectedBodyParts
    }

    @Override
    String writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final String accessionNumber = radiologyReport.fillerOrderNumber.getEi1_EntityIdentifier().value
        final CodedTriplet procedureCode = ProcedureCode.lookup(radiologyReport.study.procedureCodeId).codedTriplet
        final String procedure = "${procedureCode.codeValue.replace('\\D', '')} ${procedureCode.codeMeaning}"
        BASE_REPORT_SKELETON_HISTORICAL
            .replace(StringReplacements.ACCESSION_NUMBER_PLACEHOLDER, accessionNumber)
            .replace(StringReplacements.DATE_TIME_PLACEHOLDER, HistoricalReportStructurer.DATE_TIME_FORMATTER.format(radiologyReport.reportDateTime))
            .replace(StringReplacements.PROCEDURE, procedure)
            .replace(StringReplacements.EXAMINATION_PLACEHOLDER, examination)
            .replace(StringReplacements.HISTORY_PLACEHOLDER, history)
            .replace(StringReplacements.COMPARISON_PLACEHOLDER, comparison)
            .replace(StringReplacements.TECHNIQUE_PLACEHOLDER, technique)
            .replace(StringReplacements.FINDINGS_PLACEHOLDER, findingsFormat.parseAndSerialize(findings).join('\n'))
            .replace(StringReplacements.IMPRESSION_PLACEHOLDER, impression)
    }

    @Override
    ObxManager writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ObxManager obxGenerators = new ObxManager(
            [
                "EXAMINATION: ${examination}\n",
                "HISTORY: ${history}\n",
                "TECHNIQUE: ${technique}\n",
                "COMPARISON: ${comparison}\n"
            ].collect { ObxGenerator.forGeneralDescription(it) }
        )

        obxGenerators.add(
            ObxGenerator.forGeneralDescription(
                findingsFormat.parseAndSerialize(findings).join('\n')
            )
        )

        obxGenerators.add(ObxGenerator.forImpression("IMPRESSION:\n\n${impression}"))

        final Person interpreter = radiologyReport.getEffectivePrincipalInterpreter()
        obxGenerators.add(
            ObxGenerator.forImpression(
                "Dictated by: ${interpreter.givenNameAlphabetic} ${interpreter.familyNameAlphabetic} Interpreter, M.D."
            )
        )

        obxGenerators
    }

}
