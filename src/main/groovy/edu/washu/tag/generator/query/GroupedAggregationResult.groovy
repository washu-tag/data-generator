package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport

import java.util.function.Function

class GroupedAggregationResult extends ExpectedRadReportResult {

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

    String getDescription() {
        "A table grouped on ${primaryColumnName}"
    }

    Map<String, Map<String, Integer>> getExpectedData() {
        result
    }

    static class Case {
        String name
        Function<RadiologyReport, Boolean> aggregationCriteria

        Case(String name, Function<RadiologyReport, Boolean> aggregationCriteria) {
            this.name = name
            this.aggregationCriteria = aggregationCriteria
        }
    }

}
