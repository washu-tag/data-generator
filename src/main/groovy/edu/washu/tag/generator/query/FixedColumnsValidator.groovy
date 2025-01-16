package edu.washu.tag.generator.query

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertTrue

class FixedColumnsValidator implements LoggableValidation {

    private Map<String, List<String>> fixedColumns = [:]
    private static final Logger logger = LoggerFactory.getLogger(FixedColumnsValidator)

    FixedColumnsValidator(String columnName, String fixedValue) {
         this(columnName, [fixedValue])
    }

    FixedColumnsValidator(String columnName, List<String> permittedValues) {
        fixedColumns.put(columnName, permittedValues)
    }

    @Override
    void log() {
        logger.info(
            'Validating that each row satisfies: ' + fixedColumns.collect { column, fixedValues ->
                "Column ${column} has value " + fixedValues.size() > 1 ? "from choices: ${fixedValues}" : "'${fixedValues[0]}'"
            }.join(', ')
        )
    }

    @Override
    ForeachFunction<Row> validation() {
        { Row row ->
            fixedColumns.each { columnName, expectedValues ->
                final String actualValue = row.getString(row.fieldIndex(columnName))
                if (expectedValues.size() > 1) {
                    assertTrue(actualValue in expectedValues)
                } else {
                    assertEquals(expectedValues[0], actualValue)
                }
            }
        }
    }
}
