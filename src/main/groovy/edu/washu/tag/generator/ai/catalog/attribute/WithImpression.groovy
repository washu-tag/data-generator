package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter

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

    <S extends ReportTextBuilder<?, S>> S addImpression(S textBuilder, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE) {
        textBuilder.add('')
        if (textBuilder instanceof ModernReportTextBuilder) {
            textBuilder.beginImpression()
        }
        textBuilder.add("IMPRESSION:${delimiter.delimiter}${impression}")
    }

}