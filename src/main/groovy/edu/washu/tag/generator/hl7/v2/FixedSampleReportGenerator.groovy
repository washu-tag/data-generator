package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.util.FileIOUtils

class FixedSampleReportGenerator extends CyclicVariedGenerator {

    private static final String EXAMPLE_EXAMINATION = 'Some radiology study'
    private String findingsFile = 'fixed_small_findings.txt'
    private String impressionsFile = 'fixed_small_impressions.txt'
    private String findings
    private String impressions

    @Override
    protected List<PatientOutput> formBaseReports(List<Patient> patients, boolean temporalHeartbeat) {
        patients.collect { patient ->
            new PatientOutput(
                patientId: patient.patientIds[0].idNumber,
                generatedReports: patient.studies.collect { study ->
                    new GeneratedReport(
                        uid: study.studyInstanceUid,
                        examination: EXAMPLE_EXAMINATION,
                        findings: findings,
                        impressions: impressions
                    )
                }
            )
        }
    }

    String getFindingsFile() {
        findingsFile
    }

    void setFindingsFile(String findingsFile) {
        this.findingsFile = findingsFile
        findings = FileIOUtils.readResource(findingsFile)
    }

    String getImpressionsFile() {
        impressionsFile
    }

    void setImpressionsFile(String impressionsFile) {
        this.impressionsFile = impressionsFile
        impressions = FileIOUtils.readResource(impressionsFile)
    }

}
