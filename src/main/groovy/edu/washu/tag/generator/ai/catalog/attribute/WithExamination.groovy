package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter

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

    <S extends ReportTextBuilder<?, S>> S addExamination(S textBuilder, SectionInternalDelimiter delimiter = SectionInternalDelimiter.SPACE) {
        textBuilder.addSection('EXAMINATION', examination, delimiter)
    }

}