package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import edu.washu.tag.generator.ai.catalog.attribute.WithDiagnosisCodes
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.ai.catalog.attribute.WithFindings
import edu.washu.tag.generator.ai.catalog.attribute.WithImpression
import edu.washu.tag.generator.ai.catalog.builder.HistoricalReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study

class ClassicReport extends GeneratedReport<ClassicReport> implements
    WithExamination,
    WithImpression,
    WithFindings,
    WithDiagnosisCodes {

    ClassicReport() {
        designator = DiagnosisCodeDesignator.ICD_10
    }

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_3, ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        'Be brief in the examination section. The findings and impressions should be significantly detailed. ' + diagnosisPrompt()
    }

    @Override
    Boolean validateReport() {
        pruneCodes()
    }

    @Override
    ModernReportTextBuilder writeReportText2_3(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        writeReportText2_7(radReportMessage, radiologyReport)
    }

    @Override
    HistoricalReportTextBuilder writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final HistoricalReportTextBuilder textBuilder = new HistoricalReportTextBuilder(radiologyReport, this)
        addFindings(textBuilder)
        addImpression(textBuilder)
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder obxManager = new ModernReportTextBuilder(radiologyReport)
        addExamination(obxManager)
        addFindings(obxManager)
        addImpression(obxManager)
    }

}
