package edu.washu.tag.generator.ai

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.HistoricalReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.hl7.v2.StudyReportGenerator2_4
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

    ModernReportTextBuilder writeReportText2_3(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
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

        final List<ObxGenerator> resolvedObx = switch (reportVersion) {
            case ReportVersion.V2_3 -> {
                final ModernReportTextBuilder obxManager = writeReportText2_3(radReportMessage, radiologyReport)
                obxManager.textGenerators.eachWithIndex { obxGenerator, i ->
                    obxGenerator.setId(String.valueOf(i + 1))
                }
                obxManager.textGenerators
            }
            case ReportVersion.V2_4 -> {
                final String observationId = RandomGenUtils.randomIdStr()
                final String mainReportText = writeReportText2_4(radReportMessage, radiologyReport).compileText()
                final List<ObxGenerator> obxGenerators = StudyReportGenerator2_4.generateObx(radiologyReport, mainReportText)
                obxGenerators.eachWithIndex { obxGenerator, i ->
                    obxGenerator.observationId(observationId)
                }
                obxGenerators
            }
            case ReportVersion.V2_7 -> {
                final ModernReportTextBuilder obxManager = writeReportText2_7(radReportMessage, radiologyReport)
                if (obxManager.includeDictation) {
                    obxManager.beginImpression()
                    final int endOfImpression = obxManager.textGenerators.findLastIndexOf { generator ->
                        generator.observationIdSuffix == ObxGenerator.IMPRESSION_SUFFIX
                    } + 1
                    final Person interpreter = radiologyReport.getEffectivePrincipalInterpreter()
                    obxManager.add("Dictated by: ${interpreter.givenNameAlphabetic} ${interpreter.familyNameAlphabetic} Interpreter, M.D.", endOfImpression)
                }
                int currentObservationSubId = 0
                String currentObservationIdSuffix = null // TODO: this is a simplifying assumption for now, OBX-4 can change segment-to-segment for ADT or TCM
                obxManager.textGenerators.eachWithIndex { obxGenerator, i ->
                    obxGenerator.setId(String.valueOf(i + 2))
                    if (obxGenerator.observationIdSuffix != currentObservationIdSuffix) {
                        currentObservationIdSuffix = obxGenerator.observationIdSuffix
                        currentObservationSubId++
                    }
                    obxGenerator.setObservationSubId(String.valueOf(currentObservationSubId))
                }

                obxManager.textGenerators
            }
        }

        resolvedObx.eachWithIndex { obxGenerator, i ->
            obxGenerator.generateSegment(
                radiologyReport,
                radReportMessage.PATIENT_RESULT.ORDER_OBSERVATION.getOBSERVATION(i).OBX
            )
        }
    }

    static void throwVersion(ReportVersion reportVersion) {
        throw new UnsupportedOperationException("Version ${reportVersion} not supported")
    }

}
