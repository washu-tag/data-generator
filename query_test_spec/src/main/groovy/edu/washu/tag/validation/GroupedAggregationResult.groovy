package edu.washu.tag.validation

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.assertj.core.api.Assertions.assertThat

class GroupedAggregationResult implements ExpectedQueryResult {

    Map<String, Map<String, Integer>> result = [:]
    String primaryColumnName
    List<String> secondaryColumns
    private static final Logger logger = LoggerFactory.getLogger(GroupedAggregationResult)

    GroupedAggregationResult primaryColumn(String name) {
        primaryColumnName = name
        this
    }

    GroupedAggregationResult secondaryColumns(List<String> columns) {
        secondaryColumns = columns
        this
    }

    GroupedAggregationResult expectingResult(Map<String, Map<String, Integer>> result) {
        this.result = result
        this
    }

    @Override
    void validateResult(Dataset<Row> queryResult) {
        final List<String> expectedColumns = [(primaryColumnName)] + secondaryColumns
        logger.info("Validating that the query result columns are: ${expectedColumns}...")
        assertThat(queryResult.columns() as List<String>).isEqualTo(expectedColumns)
        logger.info("Validating that the result has ${result.size()} rows...")
        assertThat(queryResult.count()).as("query result size").isEqualTo(result.size())
        queryResult.foreach(new ForeachFunction<Row>() {
            @Override
            void call(Row row) throws Exception {
                final String primaryColumn = row.getString(0)
                final Map<String, Integer> expectation = result.get(primaryColumn)
                logger.info("Validating counts for ${primaryColumn}")
                secondaryColumns.eachWithIndex { columnName, index ->
                    assertThat(row.getLong(index + 1).intValue())
                            .as("count for ${primaryColumn} with column ${columnName}")
                            .isEqualTo(expectation.get(columnName))
                }
            }
        })
    }

}
