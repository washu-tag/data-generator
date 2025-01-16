package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory

import java.util.function.Function

import static org.testng.AssertJUnit.assertEquals

class ExactNumberRadReportResult extends ExpectedRadReportResult {

    //private static final Logger logger = LoggerFactory.getLogger(ExactNumberRadReportResult)

    ExactNumberRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    int expectedNumResults = 0
    private ForeachFunction<Row> additionalValidation

    ExactNumberRadReportResult withAdditionalValidation(ForeachFunction<Row> validation) {
        additionalValidation = validation
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectedNumResults++
    }

    @Override
    void validateResult(Dataset<Row> result) {
        //logger.info('Validating result...')
        withAdditionalValidation({ row ->
            assertEquals('F', row.getString(row.fieldIndex('pid_8_administrative_sex')))
        })
        assertEquals(expectedNumResults, result.count())
        if (additionalValidation != null) {
            result.foreach(additionalValidation)
        }
    }

}
