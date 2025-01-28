package edu.washu.tag.validation

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.assertj.core.api.Assertions.assertThat

class FixedColumnsValidator implements LoggableValidation {

    Map<String, List<String>> fixedColumns = [:]
    private static final Logger logger = LoggerFactory.getLogger(FixedColumnsValidator)

    FixedColumnsValidator(String columnName, String fixedValue) {
         this(columnName, [fixedValue])
    }

    FixedColumnsValidator(String columnName, List<String> permittedValues) {
        fixedColumns.put(columnName, permittedValues)
    }

    FixedColumnsValidator() {

    }

    FixedColumnsValidator validating(String columnName, String fixedValue) {
        validating(columnName, [fixedValue])
    }

    FixedColumnsValidator validating(String columnName, List<String> permittedValues) {
        fixedColumns.put(columnName, permittedValues)
        this
    }

    @Override
    void log() {
        logger.info(
            'Validating that each row satisfies: ' + fixedColumns.collect { column, fixedValues ->
                "Column ${column} has value " + (fixedValues.size() > 1 ? "from choices: ${fixedValues}" : "'${fixedValues[0]}'")
            }.join(', ')
        )
    }

    @Override
    ForeachFunction<Row> validation() {
        { Row row ->
            fixedColumns.each { columnName, expectedValues ->
                assertThat(row.getString(row.fieldIndex(columnName))).isIn(expectedValues)
            }
        }
    }

}
