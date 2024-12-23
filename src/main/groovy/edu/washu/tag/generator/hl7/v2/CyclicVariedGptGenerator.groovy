package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.AzureWrapper
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.metadata.Patient

class CyclicVariedGptGenerator extends ReportGenerator {

    int patientIndex = 0
    int overallReportIndex = 0
    private static final StudyReportGenerator currentReportGenerator = new CurrentStudyReportGenerator()
    private static final StudyReportGenerator historicalReportGenerator = new HistoricalStudyReportGenerator()

    @Override
    void generateReportsForPatient(Patient patient) {
        final List<GeneratedReport> reports = new AzureWrapper().generateReports(patient)

        patient.studies.each { study ->
            final MessageRequirements messageRequirements = new MessageRequirements()
                .extendedPid(overallReportIndex % 2 == 0)
                .numPatientIds(1 + (overallReportIndex % 2))
                .orcStatus(assignStatus())
                .includeObx(overallReportIndex % 17 != 0)
                .raceUnavailable(overallReportIndex % 31 == 0)

            study.setRadReport(
                    (generateCurrent() ? currentReportGenerator : historicalReportGenerator).generateReportFrom(
                            patient,
                            study,
                            messageRequirements,
                            reports.find {
                                it.uid == study.studyInstanceUid
                            }
                    )
            )
            overallReportIndex++
        }
        patientIndex++
    }

    private ReportStatus assignStatus() {
        switch (overallReportIndex % 3) {
            case 0:
                return ReportStatus.PRELIMINARY
            case 1:
                return ReportStatus.FINAL
            case 2:
                return generateCurrent() ? ReportStatus.WET_READ : ReportStatus.FINAL
        }
    }

    private boolean generateCurrent() {
        overallReportIndex % 2 == 0
    }

}
