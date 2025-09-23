package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithSingleDiagnosis implements DiagnosisAware {

    @JsonPropertyDescription('Standardized code for the diagnosis')
    @JsonProperty('code')
    private String code

    @JsonPropertyDescription('Standardized meaning of the code')
    @JsonProperty('codeMeaning')
    private String codeMeaning

    // We define the fields for code and codeMeaning instead of using DiagnosisCode to avoid exceeding the nesting limit in classes for OpenAI
    @Override
    @JsonIgnore
    List<DiagnosisCode> resolveDiagnoses() {
        [new DiagnosisCode(code: code, codeMeaning: codeMeaning)]
    }

    String getCode() {
        code
    }

    void setCode(String code) {
        this.code = code
    }

    String getCodeMeaning() {
        codeMeaning
    }

    void setCodeMeaning(String codeMeaning) {
        this.codeMeaning = codeMeaning
    }

    void preserveDiagnosisCode(WithSingleDiagnosis destination) {
        destination.setDesignator(designator)
    }

    String diagnosisPrompt() {
        'The diagnosis can be either an existing condition that would cause the patient to get the the particular type of imaging done or ' +
            'a diagnosis determined by the radiologist in the report. The codes used for both the code and codeMeaning must come ' +
            "from ${designator.fullIdentifier}. Do not invent codes outside of the ${designator.fullIdentifier} standard."
    }

}