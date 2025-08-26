package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithExamination {

    @JsonPropertyDescription('Brief description of imaging exam performed, not mentioning date')
    @JsonProperty('examination')
    private String examination

    String getExamination() {
        examination
    }

    void setExamination(String examination) {
        this.examination = examination
    }

}