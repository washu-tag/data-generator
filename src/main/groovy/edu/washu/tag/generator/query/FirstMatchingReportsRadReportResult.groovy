package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.ExpectedQueryResult

import java.util.function.Predicate

class FirstMatchingReportsRadReportResult extends ExpectedRadReportQueryProcessor implements WithColumnExtractions<FirstMatchingReportsRadReportResult> {

    ExactRowsResult expectation = new ExactRowsResult(uniqueIdColumnName: QueryUtils.COLUMN_MESSAGE_CONTROL_ID)

    FirstMatchingReportsRadReportResult(int numReportsToMatchPerCriterion, Predicate<RadiologyReport> matchCriteria) {
        this(numReportsToMatchPerCriterion, [matchCriteria])
    }

    FirstMatchingReportsRadReportResult(int numReportsToMatchPerCriterion, List<Predicate<RadiologyReport>> matchCriteria) {
        final Map<Predicate<RadiologyReport>, Integer> matcherMap = matchCriteria.collectEntries { possibility ->
            [(possibility) : numReportsToMatchPerCriterion]
        }
        inclusionCriteria = { RadiologyReport radiologyReport ->
            if (matcherMap.isEmpty()) {
                false
            } else {
                final Map.Entry<Predicate<RadiologyReport>, Integer> match = matcherMap.find { entry ->
                    entry.key.test(radiologyReport)
                }
                if (match != null) {
                    if (match.value == 1) {
                        matcherMap.remove(match.key)
                    } else {
                        matcherMap.put(match.key, match.value - 1)
                    }
                }
                match != null
            }
        }
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
