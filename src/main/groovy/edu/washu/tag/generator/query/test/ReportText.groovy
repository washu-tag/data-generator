package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.FirstMatchingReportsRadReportResult

import java.util.function.Predicate

import static edu.washu.tag.generator.query.QueryUtils.*

class ReportText extends TestQuery<BatchSpecification> {

    private static final String COLUMN_REPORT_TEXT = 'report_text'

    ReportText() {
        super('report_text', null)
        withDataProcessor(
            new FirstMatchingReportsRadReportResult(
                1,
                predicates()
            ).withColumnExtractions({ RadiologyReport radiologyReport ->
                [
                    (COLUMN_REPORT_TEXT): radiologyReport.getReportTextForQueryExport()
                ]
            })
        ).withPostProcessing({ query ->
            setSqlFindMessageControlIds(query, "${COLUMN_MESSAGE_CONTROL_ID}, ${COLUMN_REPORT_TEXT}")
        })
    }

    private Predicate<RadiologyReport> classicReportContainsSpecialInsert() {
        { RadiologyReport radiologyReport ->
            (radiologyReport.generatedReport as ClassicReport).findings.contains(SPECIAL_CHAR_INSERT)
        }
    }

    private List<Predicate<RadiologyReport>> predicates() {
        final List<Predicate<RadiologyReport>> predicates = ReportVersion.values().collect { ReportVersion version ->
            classicReportWithContentAndVersion(version).and(classicReportContainsSpecialInsert().negate())
        }
        predicates.add(0, reportOfClass(ClassicReport).and(classicReportContainsSpecialInsert()))
        predicates
    }

}
