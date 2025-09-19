package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.*
import edu.washu.tag.generator.ai.catalog.builder.HistoricalReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.*

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
        "Be brief in the examination section. The findings and impressions should be significantly detailed. When generating the findings section of the report, each string should begin with the modality, followed by a colon, and followed by the findings. The exact imaging modalities that should be included are: ${expectedModalities}. The findings array must contain exactly ${expectedModalities.size()} items, one per provided modality."
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
    HistoricalReportTextBuilder writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final HistoricalReportTextBuilder textBuilder = new HistoricalReportTextBuilder(radiologyReport, this)
        addHistory(textBuilder)
        addComparison(textBuilder)
        addTechnique(textBuilder)
        textBuilder.add(findings.join('\n'))
        addImpression(textBuilder)
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder textBuilder = new ModernReportTextBuilder(radiologyReport)
        addExamination(textBuilder, SectionInternalDelimiter.SPACE)
        addHistory(textBuilder, SectionInternalDelimiter.SPACE)
        addTechnique(textBuilder, SectionInternalDelimiter.SPACE)
        addComparison(textBuilder, SectionInternalDelimiter.SPACE)
        textBuilder.add(findings.join('\n'))
        addImpression(textBuilder, SectionInternalDelimiter.DOUBLE_NEWLINE)
    }

    private Set<String> extractModalities(Study study) {
        study.series*.modality.toSet().intersect(interpretableModalities)
    }

}
