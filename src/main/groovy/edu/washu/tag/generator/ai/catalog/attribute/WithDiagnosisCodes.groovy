package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithDiagnosisCodes implements DiagnosisAware {

    @JsonPropertyDescription('List of diagnoses for the patient')
    @JsonProperty('diagnoses')
    private List<DiagnosisCode> diagnoses

    @Override
    @JsonIgnore
    List<DiagnosisCode> resolveDiagnoses() {
        diagnoses
    }

    List<DiagnosisCode> getDiagnoses() {
        diagnoses
    }

    void setDiagnoses(List<DiagnosisCode> diagnoses) {
        this.diagnoses = diagnoses
    }

    void preserveDiagnosisCode(WithDiagnosisCodes destination) {
        destination.setDesignator(designator)
    }

    String diagnosisPrompt() {
        'The list of diagnoses should contain between 1 and 5 objects. These diagnoses can be any combination of existing conditions that would cause the ' +
            'patient to get the the particular type of imaging done or diagnoses determined by the radiologist in the report. The codes used for both the ' +
            "code and codeMeaning must come from ${designator.fullIdentifier}. Do not invent codes outside of the ${designator.fullIdentifier} standard."
    }

}