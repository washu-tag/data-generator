package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport

import java.util.function.Function

class ExactNumberRadReportResult extends ExpectedRadReportResult {

    ExactNumberRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    int expectedNumResults = 0

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectedNumResults++
    }

}
