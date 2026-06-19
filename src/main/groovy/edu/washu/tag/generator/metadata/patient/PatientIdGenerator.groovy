package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.metadata.Study

trait PatientIdGenerator {

    boolean isDefault() {
        false
    }

    // for more complex scenarios where we only want to include an ID based on a decision that requires all reports
    boolean conditionalIncludeIdOn(Study study) {
        false
    }

    abstract String nextPatientId()

    abstract PatientId materializeIdForStudy(Study study, String id)

}