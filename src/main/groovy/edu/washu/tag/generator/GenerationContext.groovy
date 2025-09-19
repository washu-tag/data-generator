package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.patient.PatientIdEncoder

class GenerationContext {

    SpecificationParameters specificationParameters
    double currentAverageStudiesPerPatient
    long previouslyGeneratedSeries
    long previouslyGeneratedStudies
    int studyCountOverride
    int studyCountMaximum
    List<PatientIdEncoder> patientIdEncoders
    int legacyStandaloneId

    int calculateStudyCountForCurrentPatient() {
        if (studyCountOverride > 0) {
            studyCountOverride
        } else if (studyCountMaximum > 0) {
            Math.min(specificationParameters.chooseNumberOfStudies(currentAverageStudiesPerPatient), studyCountMaximum)
        } else {
            specificationParameters.chooseNumberOfStudies(currentAverageStudiesPerPatient)
        }
    }

    String nextLegacyStandaloneId() {
        final String id = String.valueOf(legacyStandaloneId)
        legacyStandaloneId++
        id
    }

    Protocol chooseProtocol(Patient patient) {
        specificationParameters.chooseProtocol(previouslyGeneratedSeries == 0 ? 0.0 : previouslyGeneratedSeries / previouslyGeneratedStudies, patient)
    }

}
