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
import edu.washu.tag.generator.util.RandomGenUtils

class CombinedFindingsImpressionReport extends GeneratedReport<CombinedFindingsImpressionReport> implements
    WithExamination,
    WithHistory {

    @JsonPropertyDescription('Detailed and lengthy narrative description of images including professional conclusion of radiologist')
    String impression

    @JsonIgnore
    ImpressionFormat impressionFormat = RandomGenUtils.randomEnumValue(ImpressionFormat)

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        'Be brief in the examination section. The impression section should be significantly detailed.'
    }

    @Override
    HistoricalReportTextBuilder writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        impressionFormat = ImpressionFormat.STANDALONE
        final HistoricalReportTextBuilder textBuilder = new HistoricalReportTextBuilder(radiologyReport, this)
        addHistory(textBuilder).add(impressionFormat.serializeImpression(impression))
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder textBuilder = new ModernReportTextBuilder()
        addExamination(textBuilder)
        addHistory(textBuilder, SectionInternalDelimiter.SPACE)
        textBuilder.beginImpression()
        textBuilder.add(impressionFormat.serializeImpression(impression))
    }

}
