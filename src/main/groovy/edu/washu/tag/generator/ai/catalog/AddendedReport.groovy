package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.ai.catalog.attribute.WithFindings
import edu.washu.tag.generator.ai.catalog.attribute.WithImpression
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study

class AddendedReport extends GeneratedReport<AddendedReport> implements
    WithExamination,
    WithImpression,
    WithFindings {

    @JsonPropertyDescription('Information added to correct one of the other sections of the report')
    String addendum

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_3, ReportVersion.V2_7] // TODO: how are these done in v2.4?
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        'Be brief in the examination section. The findings and impressions should be significantly detailed. When generating the addendum, you can override any of the other sections of the report including examination, findings, impression or any combination of these. The addendum section should not be empty.'
    }

    @Override
    ModernReportTextBuilder writeReportText2_3(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        writeReportText2_7(radReportMessage, radiologyReport)
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder obxManager = new ModernReportTextBuilder(radiologyReport)
        obxManager.beginAddendum()
        obxManager.add(addendum)
        obxManager.beginGeneralDescription()
        addExamination(obxManager)
        addFindings(obxManager)
        addImpression(obxManager)
    }

}
