package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

class DiagnosisCode {

    @JsonPropertyDescription('Standardized code for the diagnosis')
    @JsonProperty('code')
    String code

    @JsonPropertyDescription('Standardized meaning of the code')
    @JsonProperty('codeMeaning')
    String codeMeaning

}
