package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

import java.util.function.Function

class ExactEnumeratedRadReportResult extends ExpectedRadReportResult {

    final List<String> expectedReports = []

    ExactEnumeratedRadReportResult(Function<RadiologyReport, Boolean> inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectedReports << "A radiology report with ID ${radiologyReport.messageControlId}".toString()
    }

    @Override
    void validateResult(Dataset<Row> result) {
        throw new UnsupportedOperationException('Not yet written')
    }

}
