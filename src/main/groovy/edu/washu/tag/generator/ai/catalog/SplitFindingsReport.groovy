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
import edu.washu.tag.generator.ai.catalog.builder.HistoricalReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.protocols.MammogramFourView
import edu.washu.tag.generator.util.RandomGenUtils

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
    HistoricalReportTextBuilder writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final HistoricalReportTextBuilder textBuilder = new HistoricalReportTextBuilder(radiologyReport, this)
        addHistory(textBuilder)
        addComparison(textBuilder)
        addTechnique(textBuilder)
        textBuilder.add('\n' + findingsFormat.parseAndSerialize(findings).join('\n'))
        addImpression(textBuilder)
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder textBuilder = new ModernReportTextBuilder()
        addExamination(textBuilder, SectionInternalDelimiter.SPACE)
        addHistory(textBuilder, SectionInternalDelimiter.SPACE)
        addTechnique(textBuilder, SectionInternalDelimiter.SPACE)
        addComparison(textBuilder, SectionInternalDelimiter.SPACE)
        textBuilder.add(findingsFormat.parseAndSerialize(findings).join('\n'))
        addImpression(textBuilder, SectionInternalDelimiter.DOUBLE_NEWLINE)
    }

}
