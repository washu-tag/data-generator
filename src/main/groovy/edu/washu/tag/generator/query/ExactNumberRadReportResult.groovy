package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactNumberObjectsResult
import edu.washu.tag.validation.ExpectedQueryResult
import edu.washu.tag.validation.LoggableValidation

import java.util.function.Function
import java.util.function.Predicate

class ExactNumberRadReportResult extends ExpectedRadReportQueryProcessor {

    int expectedNumResults = 0
    LoggableValidation additionalValidation

    ExactNumberRadReportResult(Predicate<RadiologyReport> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    ExactNumberRadReportResult() {

    }

    ExactNumberRadReportResult withAdditionalValidation(LoggableValidation additionalValidation) {
        this.additionalValidation = additionalValidation
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectedNumResults++
    }

    @Override
    ExpectedQueryResult outputExpectation() {
        new ExactNumberObjectsResult(
            expectedNumResults: expectedNumResults,
            additionalValidation: additionalValidation
        )
    }

}
