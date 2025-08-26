package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.util.FileIOUtils

class FixedSampleReportGenerator extends CyclicVariedGenerator {

    private static final String EXAMPLE_EXAMINATION = 'Some radiology study'
    private String findingsFile = 'fixed_small_findings.txt'
    private String impressionFile = 'fixed_small_impressions.txt'
    private String findings
    private String impression

    @Override
    protected List<PatientOutput> formBaseReports(List<Patient> patients, boolean temporalHeartbeat) {
        patients.collect { patient ->
            new PatientOutput(
                patientId: patient.patientIds[0].idNumber,
                generatedReports: patient.studies.collect { study ->
                    new ClassicReport(
                        uid: study.studyInstanceUid,
                        examination: EXAMPLE_EXAMINATION,
                        findings: findings,
                        impression: impression
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
        impressionFile
    }

    void setImpressionsFile(String impressionFile) {
        this.impressionFile = impressionFile
        impression = FileIOUtils.readResource(impressionFile)
    }

}
