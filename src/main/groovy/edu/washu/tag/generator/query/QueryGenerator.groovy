package edu.washu.tag.generator.query

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.TestQuery
import edu.washu.tag.TestQuerySuite
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.metadata.patient.EpicId
import edu.washu.tag.generator.metadata.patient.MainId
import edu.washu.tag.util.FileIOUtils
import edu.washu.tag.generator.util.TimeUtils
import edu.washu.tag.validation.DateComparisonValidation
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.FixedColumnsValidator
import edu.washu.tag.validation.LoggableValidation

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.function.Function

class QueryGenerator {

    public static final String TABLE_NAME = 'syntheticdata'
    public static final String COLUMN_MESSAGE_CONTROL_ID = 'message_control_id'
    private static final String COLUMN_REPORT_TEXT = 'report_text'
    private static final String VERSION_2_4_UID = '2.25.155851373268730741395170003437433181776'
    private static final String VERSION_2_7_UID = '2.25.143467293620292044279751197905759993120'
    private static final String COLUMN_HL7_VERSION = 'version_id'
    private static final String COLUMN_SEX = 'sex'
    private static final LoggableValidation VALIDATION_SEX = new FixedColumnsValidator(COLUMN_SEX, 'F')
    private static final String COLUMN_STUDY_INSTANCE_UID = 'study_instance_uid'
    private static final LoggableValidation VALIDATION_STUDY_INSTANCE_UID = new FixedColumnsValidator()
        .validating(COLUMN_STUDY_INSTANCE_UID, [null])
        .validating(COLUMN_HL7_VERSION, '2.4')
    private static final String COLUMN_DOB = 'birth_date'
    private static final LoggableValidation VALIDATION_DOB = new DateComparisonValidation()
        .description('patient born after 1990-12-31')
        .columnName(COLUMN_DOB)
        .comparisonValue(19901231)
        .comparisonOperator(DateComparisonValidation.ComparisonOperator.GT)
    private static final String COLUMN_ORC_PLACER_ORDER_NUM = 'orc_2_placer_order_number'
    private static final LoggableValidation VALIDATION_ORC_PLACER_ORDER_NUM = new FixedColumnsValidator()
        .validating(COLUMN_ORC_PLACER_ORDER_NUM, [null])
        .validating(COLUMN_HL7_VERSION, '2.4')
    private static final String COLUMN_RACE = 'race'
    private static final LoggableValidation VALIDATION_RACE = new FixedColumnsValidator()
        .validating(COLUMN_SEX, 'F')
        .validating(COLUMN_RACE, ['B', 'BLACK'])
    private static final File testQueryOutput = new File('test_queries')

    static {
        if (!testQueryOutput.exists()) {
            testQueryOutput.mkdir()
        }
    }

    private final TestQuery<BatchSpecification> patientIdQuery = new TestQuery<BatchSpecification>('patient_id', null)
        .withDataProcessor(
            new FirstPatientsRadReportResult(5)
                .withColumnExtractions({ radiologyReport ->
                    [new MainId(), new EpicId()]*.expectedColumnName().collectEntries { columnName ->
                        [(columnName): radiologyReport.patientIds.find { it.expectedColumnName() == columnName }?.idNumber]
                    }
                })
        ).withPostProcessing({ query ->
            final String guaranteedId = new MainId().expectedColumnName()
            final String patientIds = (query.querySourceDataProcessor as FirstPatientsRadReportResult)
                .expectation.rowAssertions.collect { row ->
                    "'${row.value.get(guaranteedId)}'"
                }.unique().join(', ')
            query.setSql("SELECT * FROM ${TABLE_NAME} WHERE ${guaranteedId} IN (${patientIds})")
        })

    private final List<TestQuery<BatchSpecification>> queries = [
        new TestQuery('sex_equals', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F'")
            .withDataProcessor(
                new ExactNumberRadReportResult(sexFilter(Sex.FEMALE))
                    .withAdditionalValidation(VALIDATION_SEX)
            ),
        new TestQuery('uid_missing', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_STUDY_INSTANCE_UID} IS NULL")
            .withDataProcessor(
                new ExactNumberRadReportResult(matchesHl7Version('2.4'))
                    .withAdditionalValidation(VALIDATION_STUDY_INSTANCE_UID)
            ),
        new TestQuery('dob_greater', "SELECT * FROM ${TABLE_NAME} WHERE YEAR(${COLUMN_DOB}) > 1990")
            .withDataProcessor(
                new ExactNumberRadReportResult(
                    { RadiologyReport radiologyReport ->
                        radiologyReport.patient.dateOfBirth.isAfter(LocalDate.of(1990, 12, 31))
                    }
                ).withAdditionalValidation(VALIDATION_DOB)
            ),
        new TestQuery('placer_order_missing', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_ORC_PLACER_ORDER_NUM} IS NULL")
            .withDataProcessor(
                new ExactNumberRadReportResult(matchesHl7Version('2.4'))
                    .withAdditionalValidation(VALIDATION_ORC_PLACER_ORDER_NUM)
            ),
        new TestQuery('sex_and_race_list_inclusion', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F' AND ${COLUMN_RACE} IN ('BLACK', 'B')")
            .withDataProcessor(
                new ExactNumberRadReportResult(
                    { RadiologyReport radiologyReport ->
                        radiologyReport.patient.sex == Sex.FEMALE && radiologyReport.race == Race.BLACK
                    }
                ).withAdditionalValidation(VALIDATION_RACE)
            ),
        primaryModalityBySex(),
        patientIdQuery,
        new TestQuery('all', "SELECT * FROM ${TABLE_NAME}")
            .withDataProcessor(
                new ExactNumberRadReportResult(Function.identity() as Function<RadiologyReport, Boolean>)
            )
    ]

    /**
     * The 2.4 report had some special characters inserted into it downstream, so this test is dependent on that data.
     */
    private final List<TestQuery<BatchSpecification>> hardcodedQueries = [
        new TestQuery<BatchSpecification>(
            'report_text',
            "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_MESSAGE_CONTROL_ID} IN ('${VERSION_2_4_UID}', '${VERSION_2_7_UID}')"
        ).withDataProcessor(
            new FixedResultQueryProcessor(
                new ExactRowsResult(
                    uniqueIdColumnName: COLUMN_MESSAGE_CONTROL_ID,
                    rowAssertions: [
                        (VERSION_2_4_UID): [(COLUMN_REPORT_TEXT): FileIOUtils.readResource('query_2_4_report_text.txt')],
                        (VERSION_2_7_UID): [(COLUMN_REPORT_TEXT): FileIOUtils.readResource('query_2_7_report_text.txt')]
                    ]
                )
            )
        )
    ]

    void processData(BatchSpecification batchSpecification) {
        queries.each { query ->
            query.querySourceDataProcessor.process(batchSpecification)
        }
    }

    List<TestQuery<BatchSpecification>> getTestQueries() {
        queries
    }

    void writeQueries() {
        queries.each { TestQuery<BatchSpecification> testQuery ->
            testQuery.postProcessing?.accept(testQuery)
            testQuery.setExpectedQueryResult(testQuery.querySourceDataProcessor.outputExpectation())
        }

        hardcodedQueries.each { query ->
            query.setExpectedQueryResult(query.querySourceDataProcessor.outputExpectation())
        }

        new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValue(
                new File(testQueryOutput, "queries_${TimeUtils.HL7_FORMATTER_DATETIME.format(LocalDateTime.now())}.json"),
                new TestQuerySuite(
                    viewName: TABLE_NAME,
                    testQueries: queries + hardcodedQueries
                )
            )
    }

    private static TestQuery primaryModalityBySex() {
        new TestQuery('modality_grouping', FileIOUtils.readResource('modality_query.sql'))
            .withDataProcessor(
                new GroupedAggregationRadReportResult({ true })
                    .primaryColumn('primary_modality')
                    .primaryColumnDerivation({ report ->
                        ProcedureCode.lookup(report.study.procedureCodeId).impliedModality
                    }).addCase(
                        new GroupedAggregationRadReportResult.Case(
                            'male_count',
                            sexFilter(Sex.MALE)
                        )
                    ).addCase(
                        new GroupedAggregationRadReportResult.Case(
                            'female_count',
                                sexFilter(Sex.FEMALE)
                        )
                    ).addCase(
                        new GroupedAggregationRadReportResult.Case(
                            'total_count',
                            { true }
                        )
                    )
            )
    }

    private static Function<RadiologyReport, Boolean> matchesHl7Version(String version) {
        { RadiologyReport radiologyReport ->
            radiologyReport.hl7Version == version
        }
    }

    private static Function<RadiologyReport, Boolean> sexFilter(Sex sex) {
        { RadiologyReport radiologyReport ->
            radiologyReport.patient.sex == sex
        }
    }

}
