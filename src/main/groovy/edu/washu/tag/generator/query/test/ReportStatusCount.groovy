package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObxGeneratorHistorical
import edu.washu.tag.generator.query.GroupedAggregationRadReportResult

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_REPORT_STATUS
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME

class ReportStatusCount extends TestQuery<BatchSpecification> {

    private static final Map<ReportVersion, ObxGenerator> obxGeneratorMap = [
        (ReportVersion.V2_3): new ObxGenerator(),
        (ReportVersion.V2_4): new ObxGeneratorHistorical(null),
        (ReportVersion.V2_7): new ObxGenerator()
    ]

    ReportStatusCount() {
        super('report_status_count', "SELECT ${COLUMN_REPORT_STATUS}, COUNT(*) as total_count FROM ${TABLE_NAME} GROUP BY ${COLUMN_REPORT_STATUS} ORDER BY ${COLUMN_REPORT_STATUS}")
        withDataProcessor(
            new GroupedAggregationRadReportResult({ true })
                .primaryColumn(COLUMN_REPORT_STATUS)
                .primaryColumnDerivation({ report ->
                    if (!report.includeObx) {
                        return null
                    }
                    obxGeneratorMap[report.hl7Version].getEncodedStatus(report.orcStatus)
                }).addCase(
                    new GroupedAggregationRadReportResult.Case(
                        'total_count',
                        { true }
                    )
                )
        )
    }

}
