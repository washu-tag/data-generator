package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.WithDiagnosisCodes
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.ai.catalog.attribute.WithFindings
import edu.washu.tag.generator.ai.catalog.attribute.WithImpression
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study

class DiagnosisReport extends GeneratedReport<DiagnosisReport> implements
    WithExamination,
    WithImpression,
    WithFindings,
    WithDiagnosisCodes {

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        'Be brief in the examination section. The findings and impressions should be significantly detailed. ' + diagnosisPrompt()
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder obxManager = new ModernReportTextBuilder(radiologyReport)
        addExamination(obxManager)
        addFindings(obxManager)
        addImpression(obxManager)
    }

}
