package edu.washu.tag.generator.ai

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.HistoricalReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.hl7.v2.HistoricalStudyReportGenerator
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.RandomGenUtils

abstract class GeneratedReport<T extends GeneratedReport<T>> {

    @JsonPropertyDescription('UID for imaging study')
    String uid

    List<ReportVersion> supportedVersions() {
        ReportVersion.values()
    }

    List<ObxGenerator> writeReportText2_3(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        throwVersion(ReportVersion.V2_3)
    }

    HistoricalReportTextBuilder writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        throwVersion(ReportVersion.V2_4)
    }

    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        throwVersion(ReportVersion.V2_7)
    }

    String getUserMessage(Study study, StudyRep studyRep) {
        ''
    }

    Boolean validateReport() {
        true
    }

    Boolean checkApplicability(Study study) {
        true
    }

    void preserveState(T destination) {

    }

    final void addObx(ORU_R01 radReportMessage, RadiologyReport radiologyReport, ReportVersion reportVersion) {
        if (!(reportVersion in supportedVersions())) {
            throwVersion(reportVersion)
        }

        switch (reportVersion) {
            case ReportVersion.V2_3 -> throwVersion(ReportVersion.V2_3)
            case ReportVersion.V2_4 -> {
                final String observationId = RandomGenUtils.randomIdStr()
                final String mainReportText = writeReportText2_4(radReportMessage, radiologyReport).compileText()
                HistoricalStudyReportGenerator.generateObx(radiologyReport, mainReportText).eachWithIndex { obxGenerator, i ->
                    obxGenerator
                        .observationId(observationId)
                        .generateSegment(radiologyReport, radReportMessage.PATIENT_RESULT.ORDER_OBSERVATION.getOBSERVATION(i).OBX)
                }
            }
            case ReportVersion.V2_7 -> {
                final ModernReportTextBuilder obxManager = writeReportText2_7(radReportMessage, radiologyReport)
                if (obxManager.includeDictation) {
                    obxManager.beginImpression()
                    final Person interpreter = radiologyReport.getEffectivePrincipalInterpreter()
                    obxManager.add("Dictated by: ${interpreter.givenNameAlphabetic} ${interpreter.familyNameAlphabetic} Interpreter, M.D.")
                }
                obxManager.textGenerators.eachWithIndex { obxGenerator, i ->
                    obxGenerator
                        .setId(String.valueOf(i + 2))
                        .generateSegment(radiologyReport, radReportMessage.PATIENT_RESULT.ORDER_OBSERVATION.getOBSERVATION(i).OBX)
                }
            }
        }
    }

    static void throwVersion(ReportVersion reportVersion) {
        throw new UnsupportedOperationException("Version ${reportVersion} not supported")
    }

}
