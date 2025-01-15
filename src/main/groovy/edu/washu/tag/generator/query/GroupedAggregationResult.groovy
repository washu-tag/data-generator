package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

import java.util.function.Function

import static org.testng.AssertJUnit.assertEquals

class GroupedAggregationResult extends ExpectedRadReportResult implements Serializable {

    private final Map<String, Map<String, Integer>> result = [:]
    private String primaryColumnName
    private Function<RadiologyReport, String> primaryColumnDerivation
    private List<Case> cases = []
    
    GroupedAggregationResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    GroupedAggregationResult primaryColumn(String name) {
        primaryColumnName = name
        this
    }

    GroupedAggregationResult primaryColumnDerivation(Function<RadiologyReport, String> primaryColumnDerivation) {
        this.primaryColumnDerivation = primaryColumnDerivation
        this
    }

    GroupedAggregationResult addCase(Case caseVal) {
        cases << caseVal
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        final String primaryColumnValue = primaryColumnDerivation.apply(radiologyReport)
        final Map<String, Integer> row = result.computeIfAbsent(
            primaryColumnValue,
            {
                cases.collectEntries { caseVal ->
                    [(caseVal.name) : 0]
                }
            })
        cases.each { caseVal ->
            if (caseVal.aggregationCriteria.apply(radiologyReport)) {
                row.put(caseVal.name, row[caseVal.name] + 1)
            }
        }
    }

    @Override
    void validateResult(Dataset<Row> queryResult) {
        final List<String> expectedColumns = [(primaryColumnName)]
        cases.each { caseVal ->
            expectedColumns << caseVal.name
        }
        assertEquals(
            expectedColumns,
            queryResult.columns() as List<String>
        )
        assertEquals(result.size(), queryResult.count())
        queryResult.foreach(new ForeachFunction<Row>() {
            @Override
            void call(Row row) throws Exception {
                final String primaryColumn = row.getString(0)
                final Map<String, Integer> expectation = result.get(primaryColumn)
                cases.eachWithIndex { caseVal, index ->
                    assertEquals(expectation.get(caseVal.name), row.getInt(index + 1))
                }
            }
        })
    }

    String getDescription() {
        "A table grouped on ${primaryColumnName}"
    }

    Map<String, Map<String, Integer>> getExpectedData() {
        result
    }

    static class Case implements Serializable {
        String name
        Function<RadiologyReport, Boolean> aggregationCriteria

        Case(String name, Function<RadiologyReport, Boolean> aggregationCriteria) {
            this.name = name
            this.aggregationCriteria = aggregationCriteria
        }
    }

}
