package edu.washu.tag.validation

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.assertj.core.api.Assertions.assertThat

class SingleRowResult implements ExpectedQueryResult {

    Map<String, String> exactColumnChecks = [:]
    Map<String, String> columnContainsChecks = [:]
    private static final Logger logger = LoggerFactory.getLogger(SingleRowResult)

    @Override
    void validateResult(Dataset<Row> result) {
        logger.info("Validating result is a single row with various features...")
        assertThat(result.count()).as('result size').isEqualTo(1)
        final Row row = result.collectAsList()[0]
        exactColumnChecks.each { columnName, expectedValue ->
            assertThat(row.getString(row.fieldIndex(columnName))).as("${columnName} value").isEqualTo(expectedValue)
        }
        columnContainsChecks.each { columnName, expectedValue ->
            assertThat(row.getString(row.fieldIndex(columnName))).as("${columnName} value").contains(expectedValue)
        }
    }

}
