package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.metadata.Patient

class FixedSampleReportGenerator extends CyclicVariedGenerator {

    private static final String EXAMPLE_EXAMINATION = 'Some radiology study'
    private static final String EXAMPLE_FINDINGS = 'Some sort of findings'
    private static final String EXAMPLE_IMPRESSIONS = 'Some impressions'

    @Override
    protected List<GeneratedReport> formBaseReports(Patient patient) {
        patient.studies.collect { study ->
            new GeneratedReport(
                uid: study.studyInstanceUid,
                examination: EXAMPLE_EXAMINATION,
                findings: EXAMPLE_FINDINGS,
                impressions: EXAMPLE_IMPRESSIONS
            )
        }
    }
}
