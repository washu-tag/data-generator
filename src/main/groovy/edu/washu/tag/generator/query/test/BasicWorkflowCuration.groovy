package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.FirstMatchingReportsRadReportResult
import edu.washu.tag.generator.query.SingleReportDiagnosesQueryProcessor

import java.util.function.Function
import java.util.function.Predicate

import static edu.washu.tag.generator.hl7.v2.ReportVersion.V2_7
import static edu.washu.tag.generator.query.QueryUtils.*

class BasicWorkflowCuration extends TestQuery<BatchSpecification> {

    private static final Function<RadiologyReport, Map<String, String>> columnExtractions = { RadiologyReport radiologyReport ->
        [
            'primary_study_identifier': radiologyReport.study.accessionNumber,
            'accession_number': radiologyReport.study.accessionNumber
        ]
    }
    static final int NUM_DX_IN_REPORT = 3

    static final Predicate<RadiologyReport> singleReportPredicate = classicReportWithContentAndVersion(V2_7).and(
        { (it.generatedReport as ClassicReport).getParsedCodes().size() == NUM_DX_IN_REPORT }
    )
    static final TestQuery<BatchSpecification> curatedTable = testQuery('basic_curation', SUFFIX_CURATED)
    static final TestQuery<BatchSpecification> latestTable = testQuery('basic_latest', SUFFIX_LATEST)
    static final TestQuery<BatchSpecification> dxTable = new TestQuery<BatchSpecification>('basic_dx', null)
        .withDataProcessor(
            new SingleReportDiagnosesQueryProcessor(
                singleReportPredicate
            ).withColumnExtractions(columnExtractions)
        ).withPostProcessing({ query ->
            setSqlSelectStarFindMessageControlIds(query, SUFFIX_DIAGNOSES)
        })

    private static TestQuery<BatchSpecification> testQuery(String testId, String tableSuffix) {
        new TestQuery<BatchSpecification>(testId, null)
            .withDataProcessor(
                new FirstMatchingReportsRadReportResult(
                    1,
                    singleReportPredicate
                ).withColumnExtractions(columnExtractions)
            ).withPostProcessing({ query ->
                setSqlSelectStarFindMessageControlIds(query, tableSuffix)
            })
    }

}
