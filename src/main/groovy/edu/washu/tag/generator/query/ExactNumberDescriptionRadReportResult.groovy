package edu.washu.tag.generator.query

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

import java.util.function.Function

class ExactNumberDescriptionRadReportResult extends ExactNumberRadReportResult {

    ExactNumberDescriptionRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria, String description) {
        super(inclusionCriteria)
        this.description = description
    }

    @JsonIgnore String description

    String getAssertion() {
        "${expectedNumResults} rows where each report ${description}"
    }

    @Override
    void validateResult(Dataset<Row> result) {
        super.validateResult(result)
        // TODO:
    }
    
}
