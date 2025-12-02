package edu.washu.tag.generator.query

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.TestQuery
import edu.washu.tag.TestQuerySuite
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.query.test.AllReports
import edu.washu.tag.generator.query.test.Diagnoses
import edu.washu.tag.generator.query.test.DobGreater
import edu.washu.tag.generator.query.test.ExtendedMetadata
import edu.washu.tag.generator.query.test.Names
import edu.washu.tag.generator.query.test.NullMessageDt
import edu.washu.tag.generator.query.test.PatientIdQuery
import edu.washu.tag.generator.query.test.PlacerOrderMissing
import edu.washu.tag.generator.query.test.PrimaryModalityBySex
import edu.washu.tag.generator.query.test.ReportStatusCount
import edu.washu.tag.generator.query.test.ReportText
import edu.washu.tag.generator.query.test.SexAndRaceListInclusion
import edu.washu.tag.generator.query.test.SexEquals
import edu.washu.tag.generator.query.test.SuffixInferredReportSections
import edu.washu.tag.generator.query.test.UidMissing
import edu.washu.tag.generator.util.TimeUtils

import java.time.LocalDateTime

class QueryGenerator {

    private static final File testQueryOutput = new File('test_queries')

    static {
        if (!testQueryOutput.exists()) {
            testQueryOutput.mkdir()
        }
    }

    private final List<TestQuery<BatchSpecification>> queries = [
        new SexEquals(),
        new UidMissing(),
        new DobGreater(),
        new PlacerOrderMissing(),
        new SexAndRaceListInclusion(),
        new PrimaryModalityBySex(),
        new ReportStatusCount(),
        new ExtendedMetadata(),
        new PatientIdQuery(),
        new AllReports(),
        new NullMessageDt(),
        new ReportText(),
        new SuffixInferredReportSections(),
        new Diagnoses(),
        new Names()
    ]

    void processData(BatchSpecification batchSpecification) {
        queries.each { query ->
            query.querySourceDataProcessor.process(batchSpecification)
        }
    }

    void writeQueries() {
        queries.each { testQuery ->
            testQuery.postProcessing?.accept(testQuery)
            testQuery.setExpectedQueryResult(testQuery.querySourceDataProcessor.outputExpectation())
        }

        new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValue(
                new File(testQueryOutput, "queries_${TimeUtils.HL7_FORMATTER_DATETIME.format(LocalDateTime.now())}.json"),
                new TestQuerySuite(
                    viewName: TestQuerySuite.TABLE_PLACEHOLDER,
                    testQueries: queries
                )
            )
    }

}
