package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.SectionInternalDelimiter

trait WithHistory {

    @JsonPropertyDescription('Brief description of the patient usually including age and a reason for the exam')
    @JsonProperty('history')
    private String history

    String getHistory() {
        history
    }

    void setHistory(String history) {
        this.history = history
    }

    <S extends ReportTextBuilder<?, S>> S addHistory(S textBuilder, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE) {
        textBuilder.addSection('HISTORY', history, delimiter)
    }

}