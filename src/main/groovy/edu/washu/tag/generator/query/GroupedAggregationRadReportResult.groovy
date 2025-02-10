package edu.washu.tag.generator.query

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExpectedQueryResult
import edu.washu.tag.validation.GroupedAggregationResult
import java.util.function.Function

class GroupedAggregationRadReportResult extends ExpectedRadReportQueryProcessor {

    Map<String, Map<String, Integer>> result = [:]
    String primaryColumnName
    List<Case> cases = []
    private Function<RadiologyReport, String> primaryColumnDerivation

    GroupedAggregationRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    GroupedAggregationRadReportResult() {

    }

    GroupedAggregationRadReportResult primaryColumn(String name) {
        primaryColumnName = name
        this
    }

    GroupedAggregationRadReportResult primaryColumnDerivation(Function<RadiologyReport, String> primaryColumnDerivation) {
        this.primaryColumnDerivation = primaryColumnDerivation
        this
    }

    GroupedAggregationRadReportResult addCase(Case caseVal) {
        cases << caseVal
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        final String primaryColumnValue = primaryColumnDerivation.apply(radiologyReport) ?: ''
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
    ExpectedQueryResult outputExpectation() {
        new GroupedAggregationResult()
            .primaryColumn(primaryColumnName)
            .secondaryColumns(cases*.name as List<String>)
            .expectingResult(result)
    }

    static class Case implements Serializable {
        String name
        @JsonIgnore Function<RadiologyReport, Boolean> aggregationCriteria

        Case(String name, Function<RadiologyReport, Boolean> aggregationCriteria) {
            this.name = name
            this.aggregationCriteria = aggregationCriteria
        }

        Case() {

        }
    }

}
