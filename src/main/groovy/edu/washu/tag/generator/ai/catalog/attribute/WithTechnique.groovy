package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter

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

    <S extends ReportTextBuilder<?, S>> S addTechnique(S textBuilder, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE) {
        textBuilder.addSection('TECHNIQUE', technique, delimiter)
    }

}