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

class SplitFindingsByModalityReport extends GeneratedReport<SplitFindingsByModalityReport> implements
    WithExamination,
    WithHistory,
    WithComparison,
    WithTechnique,
    WithImpression {

    @JsonPropertyDescription('Detailed and lengthy narrative description of images by imaging modality')
    List<String> findings

    @JsonIgnore
    Set<String> expectedModalities = []

    private static final String BASE_REPORT_SKELETON_HISTORICAL = FileIOUtils.readResource('legacy_split_report_skeleton.txt')
    private static final Set<String> interpretableModalities = [
        'CR', 'CT', 'US', 'MR', 'MG', 'DX', 'RF', 'XA', 'NM', 'PT'
    ]

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        expectedModalities = extractModalities(study)
        final String comparison = studyRep.compareTo == null ? 'There is no previous known comparison, so the comparison property should contain only a brief mention of no comparison available. ' : ''
        "${comparison}Be brief in the examination section. The findings and impressions should be significantly detailed. When generating the findings section of the report, each string should begin with the modality, followed by a colon, and followed by the findings. The exact imaging modalities that should be included are: ${expectedModalities}. The findings array must contain exactly ${expectedModalities.size()} items, one per provided modality."
    }

    @Override
    void preserveState(SplitFindingsByModalityReport destination) {
        destination.setExpectedModalities(expectedModalities)
    }

    @Override
    Boolean checkApplicability(Study study) {
        extractModalities(study).size() > 1
    }

    @Override
    Boolean validateReport() {
        final Set<String> inferredModalities = findings.collect {
            it.split(':')[0].toUpperCase()
        }
        if (inferredModalities == expectedModalities) {
            findings = findings.collect { finding ->
                final int colonIndex = finding.indexOf(':')
                "${finding.substring(0, colonIndex)} FINDINGS: ${finding.substring(colonIndex + 1).trim()}".toString()
            }
            true
        } else {
            false
        }
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
            .replace(StringReplacements.FINDINGS_PLACEHOLDER, findings.join('\n'))
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
                findings.join('\n')
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

    private Set<String> extractModalities(Study study) {
        study.series*.modality.toSet().intersect(interpretableModalities)
    }

}
