package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

import java.util.function.Function

import static org.testng.AssertJUnit.assertEquals

class ExactNumberRadReportResult extends ExpectedRadReportResult {

    ExactNumberRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    int expectedNumResults = 0

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectedNumResults++
    }

    @Override
    void validateResult(Dataset<Row> result) {
        assertEquals(expectedNumResults, result.count())
    }

}
