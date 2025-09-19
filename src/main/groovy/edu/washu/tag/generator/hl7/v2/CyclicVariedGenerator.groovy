package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.hl7.v2.model.TransportationMode
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study

abstract class CyclicVariedGenerator extends ReportGenerator {

    int patientIndex = 0
    int overallReportIndex = 0
    private static final StudyReportGenerator currentReportGenerator = new CurrentStudyReportGenerator()
    private static final StudyReportGenerator reportGenerator2_4 = new StudyReportGenerator2_4()
    private static final StudyReportGenerator reportGenerator2_3 = new StudyReportGenerator2_3()
    private static final List<StudyReportGenerator> reportGenerators = [currentReportGenerator, reportGenerator2_4]

    @Override
    void generateReportsForPatients(List<Patient> patients, boolean temporalHeartbeat) {
        final List<PatientOutput> output = formBaseReports(patients, temporalHeartbeat)

        patients.each { patient ->
            final List<GeneratedReport> reports = output.find { patientOutput ->
                patientOutput.patientId == patient.patientIds[0].idNumber
            }.generatedReports

            patient.studies.each { study ->
                final MessageRequirements messageRequirements = new MessageRequirements()
                    .extendedPid(overallReportIndex % 2 == 0)
                    .numPatientIds(1 + (overallReportIndex % 2))
                    .orcStatus(assignStatus())
                    .includeObx(overallReportIndex % 17 != 0)
                    .raceUnavailable(overallReportIndex % 31 == 0)
                    .transportationMode(generateTransportationMode(study))

                final GeneratedReport generatedReport = reports.find {
                    it.uid == study.studyInstanceUid
                }

                study.setRadReport(
                    deriveReportGenerator(generatedReport).generateReportFrom(
                        patient,
                        study,
                        messageRequirements,
                        generatedReport
                    )
                )
                overallReportIndex++
            }
            patientIndex++
        }
    }

    protected abstract List<PatientOutput> formBaseReports(List<Patient> patient, boolean temporalHeartbeat)

    protected ReportStatus assignStatus() {
        switch (overallReportIndex % 3) {
            case 0:
                return ReportStatus.PRELIMINARY
            case 1:
                return ReportStatus.FINAL
            case 2:
                return overallReportIndex % 2 == 0 ? ReportStatus.WET_READ : ReportStatus.FINAL
        }
    }

    protected TransportationMode generateTransportationMode(Study study) {
        final List<String> modalitiesInStudy = study.series*.modality
        if (modalitiesInStudy.contains('CR') || modalitiesInStudy.contains('DX')) {
            return TransportationMode.PORTABLE // not a perfect assumption, but probably ok
        }
        return switch (overallReportIndex % 47) {
            case 0 .. 30 -> TransportationMode.AMBULATORY
            case 31 .. 41 -> TransportationMode.STRETCHER
            case 42 .. 44 -> TransportationMode.WHEELCHAIR
            default -> null
        }
    }

    protected StudyReportGenerator deriveReportGenerator(GeneratedReport generatedReport) {
        final StudyReportGenerator preferredGenerator = switch (overallReportIndex % 5) {
            case [0, 3] -> currentReportGenerator
            case [1, 4] -> reportGenerator2_4
            case 2 -> reportGenerator2_3
        }
        if (preferredGenerator.checkReportCompatibility(generatedReport)) {
            preferredGenerator
        } else {
            reportGenerators.find { generator ->
                generator.checkReportCompatibility(generatedReport)
            }
        }
    }

}
