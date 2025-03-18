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
    List<PatientIdEncoder> patientIdEncoders

    int calculateStudyCountForCurrentPatient() {
        if (studyCountOverride > 0) {
            studyCountOverride
        } else {
            specificationParameters.chooseNumberOfStudies(currentAverageStudiesPerPatient)
        }
    }

    Protocol chooseProtocol(Patient patient) {
        specificationParameters.chooseProtocol(previouslyGeneratedSeries == 0 ? 0.0 : previouslyGeneratedSeries / previouslyGeneratedStudies, patient)
    }

}
