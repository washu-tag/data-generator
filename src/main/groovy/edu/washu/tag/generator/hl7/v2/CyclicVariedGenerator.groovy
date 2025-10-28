package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.CustomGeneratedReportGuarantee
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.hl7.v2.model.TransportationMode
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.patient.EmpiId
import edu.washu.tag.generator.metadata.patient.PatientId
import io.temporal.workflow.Workflow
import org.slf4j.Logger

abstract class CyclicVariedGenerator extends ReportGenerator {

    int patientIndex = 0
    int overallReportIndex = 0
    private static final StudyReportGenerator currentReportGenerator = new CurrentStudyReportGenerator()
    private static final StudyReportGenerator reportGenerator2_4 = new StudyReportGenerator2_4()
    private static final StudyReportGenerator reportGenerator2_3 = new StudyReportGenerator2_3()
    private static final List<StudyReportGenerator> reportGenerators = [currentReportGenerator, reportGenerator2_4]
    private static final Logger logger = Workflow.getLogger(CyclicVariedGenerator)

    @Override
    void generateReportsForPatients(List<Patient> patients, boolean temporalHeartbeat, List<CustomGeneratedReportGuarantee> reportGuarantees = []) {
        final List<PatientOutput> output = formBaseReports(patients, temporalHeartbeat)

        patients.each { patient ->
            final List<GeneratedReport> reports = output.find { patientOutput ->
                patientOutput.patientId == patient.patientIds[0].idNumber
            }.generatedReports

            patient.studies.each { study ->
                final MessageRequirements messageRequirements = new MessageRequirements()
                    .extendedPid(overallReportIndex % 2 == 0)
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

        reportGuarantees.each { reportGuarantee ->
            reportGuarantee.reportVersions.each { reportVersion ->
                if (!reportGuaranteeSatisfied(patients, reportGuarantee.reportClass, reportVersion)) {
                    logger.info("Could not find an example of ${reportGuarantee.reportClass.simpleName} with version ${reportVersion.hl7Version}. " +
                        'Overwriting an existing report...')
                    overwriteReport(
                        findOverrideableReport(patients, reportVersion),
                        reportGuarantee.reportClass
                    )
                } else {
                    logger.info("Found an example of ${reportGuarantee.reportClass.simpleName} with version ${reportVersion.hl7Version}")
                }
            }
        }

        patients.each { patient ->
            patient.studies.each { study ->
                study.radReport.generatedReport.postprocessReport(study.radReport)
            }
        }

        patients.each { patient ->
            if (patient.studies.any { it.radReport.hl7Version == ReportVersion.V2_3 }) {
                final PatientId mpi = new EmpiId(idNumber: patient.legacyPatientId)
                patient.studies*.radReport.each { radReport ->
                    if (radReport.hl7Version == ReportVersion.V2_7) {
                        radReport.patientIds << mpi
                    }
                }
            }
        }
    }

    protected abstract List<PatientOutput> formBaseReports(List<Patient> patient, boolean temporalHeartbeat)

    protected abstract overwriteReport(RadiologyReport reportToOverwrite, Class<? extends GeneratedReport> reportClass)

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

    protected boolean reportGuaranteeSatisfied(List<Patient> patients, Class<? extends GeneratedReport> reportClass, ReportVersion reportVersion) {
        patients.any { patient ->
            patient.studies.any { study ->
                final RadiologyReport radiologyReport = study.radReport
                reportClass.isInstance(radiologyReport.generatedReport) && radiologyReport.hl7Version == reportVersion
            }
        }
    }

    protected RadiologyReport findOverrideableReport(List<Patient> patients, ReportVersion reportVersion) {
        for (Patient patient : patients) {
            for (Study study : patient.studies) {
                final RadiologyReport report = study.radReport
                if (report.includeObx && report.generatedReport instanceof ClassicReport && report.hl7Version == reportVersion) {
                    return report
                }
            }
        }
        throw new RuntimeException('Could not find a report to overwrite')
    }

}
