package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithImpression {

    @JsonPropertyDescription("Radiologist's detailed conclusions based on findings")
    @JsonProperty('impression')
    private String impression

    String getImpression() {
        impression
    }

    void setImpression(String impression) {
        this.impression = impression
    }

}