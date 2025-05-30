package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.ExpectedQueryResult
import edu.washu.tag.validation.column.ColumnType

import java.util.function.Function

class FirstMatchingReportsRadReportResult extends ExpectedRadReportQueryProcessor {

    int numReportsToMatch = 0
    Function<RadiologyReport, Map<String, String>> columnExtractions = { [:] }
    Set<ColumnType<?>> columnTypes = []
    ExactRowsResult expectation = new ExactRowsResult(uniqueIdColumnName: QueryGenerator.COLUMN_MESSAGE_CONTROL_ID)
    private int currentNumMatched = 0

    FirstMatchingReportsRadReportResult(int numReportsToMatch, Function<RadiologyReport, Boolean> matchCriteria) {
        this.numReportsToMatch = numReportsToMatch
        inclusionCriteria = { RadiologyReport radiologyReport ->
            matchCriteria.apply(radiologyReport) && currentNumMatched++ < numReportsToMatch
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
