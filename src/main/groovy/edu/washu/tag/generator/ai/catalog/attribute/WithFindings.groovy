package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription

trait WithFindings {

    @JsonPropertyDescription('Detailed and lengthy narrative description of images')
    @JsonProperty('findings')
    private String findings

    String getFindings() {
        findings
    }

    void setFindings(String findings) {
        this.findings = findings
    }

}