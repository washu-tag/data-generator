package edu.washu.tag.validation

import edu.washu.tag.validation.column.ColumnType
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.assertj.core.api.Assertions.assertThat

class ExactRowsResult implements ExpectedQueryResult {

    String uniqueIdColumnName
    Map<String, Map<String, String>> rowAssertions = [:]
    Set<ColumnType<?>> columnTypes = []
    private static final Logger logger = LoggerFactory.getLogger(ExactRowsResult)

    @Override
    void validateResult(Dataset<Row> result) {
        logger.info("Validating unique IDs in result...")
        assertThat(result.select(uniqueIdColumnName).as(Encoders.STRING()).collectAsList())
            .as('unique keys')
            .hasSameElementsAs(rowAssertions.keySet())
        result.foreach(new RowValidationFunction())
    }

    private class RowValidationFunction implements ForeachFunction<Row> {
        private static final Logger functionLogger = LoggerFactory.getLogger(RowValidationFunction)

        @Override
        void call(Row row) throws Exception {
            final String uniqueId = row.getString(row.fieldIndex(uniqueIdColumnName))
            rowAssertions.get(uniqueId).each { columnName, expectedValue ->
                functionLogger.info("Validating value for column ${columnName}")
                final int columnIndex = row.fieldIndex(columnName)
                final ColumnType<?> existingMapping = columnTypes.find { columnType ->
                    columnType.columnName == columnName
                }
                if (existingMapping != null) {
                    assertThat(existingMapping.readValue(row, columnIndex))
                        .as(columnName)
                        .isEqualTo(expectedValue)
                } else {
                    assertThat(row.getString(row.fieldIndex(columnName)))
                        .as(columnName)
                        .isEqualTo(expectedValue)
                }
            }
        }
    }

}
