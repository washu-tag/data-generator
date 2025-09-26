package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithDiagnosisCodes {

    @JsonPropertyDescription('List of diagnosis codes for the patient')
    @JsonProperty('diagnoses')
    private String diagnoses

    private List<String> parsedDiagnoses

    @JsonIgnore
    DiagnosisCodeDesignator designator = DiagnosisCodeDesignator.randomize()

    String getDiagnoses() {
        diagnoses
    }

    void setDiagnoses(String diagnoses) {
        this.diagnoses = diagnoses
    }

    @JsonIgnore
    List<String> getParsedCodes() {
        pruneCodes()
        parsedDiagnoses
    }

    @JsonIgnore
    // GPT is OK at producing codes, but not perfect by a long shot. To avoid retry loops where it generates 3 codes
    // where only 2 are valid over and over again until we hit a retry limit, we'll just filter to only the codes
    // that are valid, and if that keeps at least 1, we'll say good enough.
    boolean pruneCodes() {
        if (parsedDiagnoses != null) {
            return true
        }
        final List<String> split = diagnoses.split(',')*.trim()
        final List<String> valid = split.findAll { code ->
            designator.validateCode(code)
        }
        if (valid.size() > 0) {
            parsedDiagnoses = valid
            true
        } else {
            false
        }
    }

    @JsonIgnore
    String diagnosisPrompt() {
        'The diagnoses should contain between 1 and 5 codes separated by commas. These diagnoses can be any combination of existing conditions that would ' +
            'cause the patient to get the the particular type of imaging done or diagnoses determined by the radiologist in the report. The codes used must come ' +
            "from ${designator.fullIdentifier}. Do not invent codes outside of the ${designator.fullIdentifier} standard. " +
            'Do not use "header" codes that are invalid for HIPAA-covered transactions. Use fully qualified codes instead.'
    }

}