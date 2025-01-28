package edu.washu.tag.validation

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.testng.AssertJUnit.assertEquals

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
        assertEquals(
            expectedColumns,
            queryResult.columns() as List<String>
        )
        logger.info("Validating that the result has ${result.size()} rows...")
        assertEquals(result.size(), queryResult.count())
        queryResult.foreach(new ForeachFunction<Row>() {
            @Override
            void call(Row row) throws Exception {
                final String primaryColumn = row.getString(0)
                final Map<String, Integer> expectation = result.get(primaryColumn)
                logger.info("Validating counts for ${primaryColumn}")
                secondaryColumns.eachWithIndex { columnName, index ->
                    assertEquals(expectation.get(columnName), row.getLong(index + 1).intValue())
                }
            }
        })
    }

}
