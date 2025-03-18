package edu.washu.tag.validation

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
    private static final Logger logger = LoggerFactory.getLogger(ExactRowsResult)

    @Override
    void validateResult(Dataset<Row> result) {
        logger.info("Validating unique IDs in result...")
        assertThat(result.select(uniqueIdColumnName).as(Encoders.STRING()).collectAsList())
            .as('unique keys')
            .hasSameElementsAs(rowAssertions.keySet())
        result.foreach(new ForeachFunction<Row>() {
            @Override
            void call(Row row) throws Exception {
                final String uniqueId = row.getString(row.fieldIndex(uniqueIdColumnName))
                rowAssertions.get(uniqueId).each { columnName, expectedValue ->
                    assertThat(row.getString(row.fieldIndex(columnName)))
                        .as(columnName)
                        .isEqualTo(expectedValue)
                }
            }
        })
    }

}
