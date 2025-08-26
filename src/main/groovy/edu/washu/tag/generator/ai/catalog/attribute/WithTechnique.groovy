package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithTechnique {

    @JsonPropertyDescription('More detailed description on the particular imaging performed')
    @JsonProperty('technique')
    private String technique

    String getTechnique() {
        technique
    }

    void setTechnique(String technique) {
        this.technique = technique
    }

}