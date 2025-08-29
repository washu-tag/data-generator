package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter

trait WithComparison {

    @JsonPropertyDescription('A brief description of a previous study with its date')
    @JsonProperty('comparison')
    private String comparison

    String getComparison() {
        comparison
    }

    void setComparison(String comparison) {
        this.comparison = comparison
    }

    <S extends ReportTextBuilder<?, S>> S addComparison(S textBuilder, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE) {
        textBuilder.addSection('COMPARISON', comparison, delimiter)
    }

}