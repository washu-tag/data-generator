package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Function

import static org.testng.AssertJUnit.assertEquals

class ExactNumberRadReportResult extends ExpectedRadReportResult {

    private static final Logger logger = LoggerFactory.getLogger(ExactNumberRadReportResult)

    ExactNumberRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    int expectedNumResults = 0
    private LoggableValidation additionalValidation

    ExactNumberRadReportResult withAdditionalValidation(LoggableValidation validation) {
        additionalValidation = validation
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectedNumResults++
    }

    @Override
    void validateResult(Dataset<Row> result) {
        logger.info("Validating count of result to be ${expectedNumResults}")
        assertEquals(expectedNumResults, result.count())
        if (additionalValidation != null) {
            additionalValidation.log()
            result.foreach(additionalValidation.validation())
        }
    }

}
