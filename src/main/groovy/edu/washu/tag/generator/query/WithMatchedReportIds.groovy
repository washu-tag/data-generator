package edu.washu.tag.generator.query

import com.fasterxml.jackson.annotation.JsonIgnore

trait WithMatchedReportIds {

    @JsonIgnore
    List<String> matchedReportIds = []

}