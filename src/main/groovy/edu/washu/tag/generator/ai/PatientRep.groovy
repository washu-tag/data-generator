package edu.washu.tag.generator.ai

import com.fasterxml.jackson.annotation.JsonPropertyDescription

class PatientRep {

    @JsonPropertyDescription('Sex of the patient')
    String sex
    @JsonPropertyDescription('Date of birth of the patient')
    String dateOfBirth
    @JsonPropertyDescription('ID for the patient')
    String patientId
    @JsonPropertyDescription('List of imaging studies for the patient')
    List<StudyRep> studies = []

}
