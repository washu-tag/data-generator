package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.ExpectedQueryResult
import edu.washu.tag.validation.column.ColumnType

import java.util.function.Function
import java.util.function.Predicate

class FirstMatchingReportsRadReportResult extends ExpectedRadReportQueryProcessor {

    Function<RadiologyReport, Map<String, String>> columnExtractions = { [:] }
    Set<ColumnType<?>> columnTypes = []
    ExactRowsResult expectation = new ExactRowsResult(uniqueIdColumnName: QueryUtils.COLUMN_MESSAGE_CONTROL_ID)
    private int currentNumMatched = 0
    private final int numReportsToMatchPerCriterion
    private final int totalNumReportsToMatch

    FirstMatchingReportsRadReportResult(int numReportsToMatchPerCriterion, Predicate<RadiologyReport> matchCriteria) {
        this(numReportsToMatchPerCriterion, [matchCriteria])
    }

    FirstMatchingReportsRadReportResult(int numReportsToMatchPerCriterion, List<Predicate<RadiologyReport>> matchCriteria) {
        this.numReportsToMatchPerCriterion = numReportsToMatchPerCriterion
        totalNumReportsToMatch = numReportsToMatchPerCriterion * matchCriteria.size()
        inclusionCriteria = { RadiologyReport radiologyReport ->
            if (currentNumMatched >= totalNumReportsToMatch) {
                false
            } else if (matchCriteria[currentNumMatched / numReportsToMatchPerCriterion].test(radiologyReport)) {
                currentNumMatched++
                true
            } else {
                false
            }
        }
    }

    FirstMatchingReportsRadReportResult withColumnExtractions(Function<RadiologyReport, Map<String, String>> columnExtractions) {
        this.columnExtractions = columnExtractions
        this
    }

    FirstMatchingReportsRadReportResult withColumnTypes(Set<ColumnType<?>> columnTypes) {
        this.columnTypes = columnTypes
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectation.rowAssertions.put(
            radiologyReport.messageControlId,
            columnExtractions.apply(radiologyReport)
        )
    }

    @Override
    ExpectedQueryResult outputExpectation() {
        expectation.setColumnTypes(columnTypes)
        expectation
    }

}
