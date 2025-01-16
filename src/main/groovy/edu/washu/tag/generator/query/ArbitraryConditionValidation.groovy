package edu.washu.tag.generator.query

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ArbitraryConditionValidation implements LoggableValidation {

    private String description
    private ForeachFunction<Row> function
    private static final Logger logger = LoggerFactory.getLogger(ArbitraryConditionValidation)

    ArbitraryConditionValidation(String validationDescription, ForeachFunction<Row> validationFunction) {
        description = validationDescription
        function = validationFunction
    }

    @Override
    void log() {
        logger.info("Validating that each row satisfies: ${description}")
    }

    @Override
    ForeachFunction<Row> validation() {
        function
    }

}
