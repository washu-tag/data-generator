package edu.washu.tag.generator.ai

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyDescription

class StudyRep {

    @JsonPropertyDescription('UID for the study')
    String uid
    @JsonPropertyDescription('Short description of the imaging study')
    String description
    @JsonPropertyDescription('The UID of another study to which this study should be compared')
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String compareTo
    @JsonPropertyDescription('Date of this imaging study')
    String studyDate

}
