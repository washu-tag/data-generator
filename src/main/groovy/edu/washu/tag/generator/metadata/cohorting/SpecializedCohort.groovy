package edu.washu.tag.generator.metadata.cohorting

class SpecializedCohort {

    String name
    int numPatients
    List<StudyRequest> trajectory

    /**
     * Every study in a trajectory has its diagnosis codes randomized, so each step must have codes to sample from.
     * The one exception is a step using ONLY_ONE_FIXED_FOR_PATIENT after an earlier step with that strategy has
     * already fixed a code for the patient: that step reuses the fixed code (and ignores any codes it lists).
     */
    void validateDiagnosisCodes() {
        boolean patientCodeFixed = false
        trajectory.each { studyRequest ->
            final boolean hasCodes = studyRequest.diagnosisCodes || studyRequest.diagnosisCodesWeighted
            if (studyRequest.codeStrategy == CodeStrategy.ONLY_ONE_FIXED_FOR_PATIENT) {
                if (!hasCodes && !patientCodeFixed) {
                    throw new IllegalArgumentException(
                        "Cohort '${name}' step '${studyRequest.name}' uses ${CodeStrategy.ONLY_ONE_FIXED_FOR_PATIENT} but specifies no diagnosis codes, " +
                            'and no earlier step fixes a code for the patient. Add diagnosisCodes or diagnosisCodesWeighted to the first step using this strategy.'
                    )
                }
                patientCodeFixed = true
            } else if (!hasCodes) {
                throw new IllegalArgumentException(
                    "Cohort '${name}' step '${studyRequest.name}' uses ${studyRequest.codeStrategy}, which requires diagnosisCodes or diagnosisCodesWeighted."
                )
            }
        }
    }

    /**
     * Checks that every requested report class name resolves. Whether a class applies to the generated study is checked
     * when the report is generated, since applicability can depend on the study's series and body part.
     */
    void validateReportClasses() {
        trajectory.each { studyRequest ->
            try {
                studyRequest.resolveReportClasses()
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cohort '${name}' step '${studyRequest.name}': ${e.message}", e)
            }
        }
    }

    boolean requestsReportClasses() {
        trajectory.any { it.reportClasses }
    }

}
