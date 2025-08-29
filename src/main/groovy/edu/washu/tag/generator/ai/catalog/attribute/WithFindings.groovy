package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter

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

    <S extends ReportTextBuilder<?, S>> S addFindings(S textBuilder, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE) {
        textBuilder.addSection('FINDINGS', findings, delimiter)
    }

}