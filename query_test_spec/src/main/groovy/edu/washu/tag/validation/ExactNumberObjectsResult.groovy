package edu.washu.tag.validation

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.assertj.core.api.Assertions.assertThat

class ExactNumberObjectsResult implements ExpectedQueryResult {

    private static final Logger logger = LoggerFactory.getLogger(ExactNumberObjectsResult)

    int expectedNumResults = 0
    LoggableValidation additionalValidation

    ExactNumberObjectsResult withAdditionalValidation(LoggableValidation validation) {
        additionalValidation = validation
        this
    }

    @Override
    void validateResult(Dataset<Row> result) {
        logger.info("Validating count of result to be ${expectedNumResults}")
        assertThat(result.count()).as('result count').isEqualTo(expectedNumResults)
        if (additionalValidation != null) {
            additionalValidation.log()
            result.foreach(additionalValidation.validation())
        }
    }

}
