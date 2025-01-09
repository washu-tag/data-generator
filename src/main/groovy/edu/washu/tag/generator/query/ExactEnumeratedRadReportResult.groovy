package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport

import java.util.function.Function

class ExactEnumeratedRadReportResult extends ExpectedRadReportResult {

    final List<String> expectedReports = []

    ExactEnumeratedRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        reports << "A radiology report with ID ${radiologyReport.messageControlId}".toString()
    }

}
