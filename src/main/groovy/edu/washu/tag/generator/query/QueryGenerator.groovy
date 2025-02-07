package edu.washu.tag.generator.query

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.TestQuery
import edu.washu.tag.TestQuerySuite
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.util.FileIOUtils
import edu.washu.tag.generator.util.TimeUtils
import edu.washu.tag.validation.DateComparisonValidation
import edu.washu.tag.validation.FixedColumnsValidator
import edu.washu.tag.validation.LoggableValidation

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.function.Function

class QueryGenerator {

    public static final String TABLE_NAME = 'syntheticdata'
    private static final String COLUMN_HL7_VERSION = 'msh_12_version_id'
    private static final String COLUMN_SEX = 'pid_8_administrative_sex'
    private static final LoggableValidation VALIDATION_SEX = new FixedColumnsValidator(COLUMN_SEX, 'F')
    private static final String COLUMN_STUDY_INSTANCE_UID = 'zds_1_study_instance_uid'
    private static final LoggableValidation VALIDATION_STUDY_INSTANCE_UID = new FixedColumnsValidator()
        .validating(COLUMN_STUDY_INSTANCE_UID, 'None')
        .validating(COLUMN_HL7_VERSION, '2.4')
    private static final String COLUMN_DOB = 'pid_7_date_time_of_birth'
    private static final LoggableValidation VALIDATION_DOB = new DateComparisonValidation()
        .description('patient born after 1990-12-31')
        .columnName(COLUMN_DOB)
        .truncation(8)
        .comparisonValue(19901231)
        .comparisonOperator(DateComparisonValidation.ComparisonOperator.GT)
    private static final String COLUMN_ORC_PLACER_ORDER_NUM = 'orc_2_placer_order_number'
    private static final LoggableValidation VALIDATION_ORC_PLACER_ORDER_NUM = new FixedColumnsValidator()
        .validating(COLUMN_ORC_PLACER_ORDER_NUM, 'None')
        .validating(COLUMN_HL7_VERSION, '2.4')
    private static final String COLUMN_RACE = 'pid_10_race'
    private static final LoggableValidation VALIDATION_RACE = new FixedColumnsValidator()
        .validating(COLUMN_SEX, 'F')
        .validating(COLUMN_RACE, ['B', 'BLACK'])
    private static final File testQueryOutput = new File('test_queries')

    static {
        if (!testQueryOutput.exists()) {
            testQueryOutput.mkdir()
        }
    }

    private final List<TestQuery<BatchSpecification>> queries = [
        new TestQuery('sex_equals', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F'")
            .withDataProcessor(
                new ExactNumberRadReportResult(sexFilter(Sex.FEMALE))
                    .withAdditionalValidation(VALIDATION_SEX)
            ),
        new TestQuery('uid_missing', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_STUDY_INSTANCE_UID}='None'") // TODO: 'None'? Huh?
            .withDataProcessor(
                new ExactNumberRadReportResult(matchesHl7Version('2.4'))
                    .withAdditionalValidation(VALIDATION_STUDY_INSTANCE_UID)
            ),
        new TestQuery('dob_greater', "SELECT * FROM ${TABLE_NAME} WHERE SUBSTRING(${COLUMN_DOB}, 1, 8) > '19901231'")
            .withDataProcessor(
                new ExactNumberRadReportResult(
                    { RadiologyReport radiologyReport ->
                        radiologyReport.patient.dateOfBirth.isAfter(LocalDate.of(1990, 12, 31))
                    }
            ).withAdditionalValidation(VALIDATION_DOB)),
        new TestQuery('placer_order_missing', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_ORC_PLACER_ORDER_NUM}='None'") // TODO: 'None'? Huh?
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
            ).withAdditionalValidation(VALIDATION_RACE)),
        primaryModalityBySex()
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
            testQuery.setExpectedQueryResult(testQuery.querySourceDataProcessor.outputExpectation())
        }

        new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValue(
                new File(testQueryOutput, "queries_${TimeUtils.HL7_FORMATTER_DATETIME.format(LocalDateTime.now())}.json"),
                new TestQuerySuite(
                    viewName: TABLE_NAME,
                    testQueries: queries
                )
            )
    }

    private static TestQuery primaryModalityBySex() {
        new TestQuery('modality_grouping', FileIOUtils.readResource('modality_query.sql'))
            .withDataProcessor(
                new GroupedAggregationRadReportResult(matchesHl7Version('2.7'))
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
