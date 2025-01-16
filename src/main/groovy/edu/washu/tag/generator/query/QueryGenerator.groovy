package edu.washu.tag.generator.query

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.util.FileIOUtils
import org.apache.spark.sql.Row

import java.time.LocalDate
import java.util.function.Function

import static org.testng.AssertJUnit.assertTrue

class QueryGenerator {

    private static final String TABLE_NAME = 'syntheticdata'
    private static final String COLUMN_HL7_VERSION = 'msh_12_version_id'
    private static final String COLUMN_SEX = 'pid_8_administrative_sex'
    private static final LoggableValidation VALIDATION_SEX = new FixedColumnsValidator(COLUMN_SEX, 'F')
    private static final String COLUMN_STUDY_INSTANCE_UID = 'zds_1_study_instance_uid'
    private static final LoggableValidation VALIDATION_STUDY_INSTANCE_UID = new FixedColumnsValidator()
        .validating(COLUMN_STUDY_INSTANCE_UID, 'None')
        .validating(COLUMN_HL7_VERSION, '2.4')
    private static final String COLUMN_DOB = 'pid_7_date_time_of_birth'
    private static final LoggableValidation VALIDATION_DOB = new ArbitraryConditionValidation(
        'patient born after 1990-12-31',
        { Row row ->
            assertTrue(Integer.parseInt(row.getString(row.fieldIndex(COLUMN_DOB)).substring(0, 8)) > 19901231)
        }
    )
    private static final String COLUMN_ORC_PLACER_ORDER_NUM = 'orc_2_placer_order_number'
    private static final LoggableValidation VALIDATION_ORC_PLACER_ORDER_NUM = new FixedColumnsValidator()
        .validating(COLUMN_ORC_PLACER_ORDER_NUM, 'None')
        .validating(COLUMN_HL7_VERSION, '2.4')
    private static final String COLUMN_RACE = 'pid_10_race'
    private static final LoggableValidation VALIDATION_RACE = new FixedColumnsValidator()
        .validating(COLUMN_SEX, 'F')
        .validating(COLUMN_RACE, ['B', 'BLACK'])

    private final List<TestQuery> queries = [
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F'")
            .expecting(
                new ExactNumberRadReportResult(sexFilter(Sex.FEMALE))
                    .withAdditionalValidation(VALIDATION_SEX)
            ),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_STUDY_INSTANCE_UID}='None'") // TODO: 'None'? Huh?
            .expecting(new ExactNumberDescriptionRadReportResult(
                matchesHl7Version('2.4'),
                'has a null zds_1_study_instance_uid column'
            ).withAdditionalValidation(VALIDATION_STUDY_INSTANCE_UID)),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE SUBSTRING(${COLUMN_DOB}, 1, 8) > '19901231'")
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.dateOfBirth.isAfter(LocalDate.of(1990, 12, 31))
                }, 'corresponds to a patient born after 1990-12-31'
            ).withAdditionalValidation(VALIDATION_DOB)),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_ORC_PLACER_ORDER_NUM}='None'") // TODO: 'None'? Huh?
            .expecting(new ExactNumberDescriptionRadReportResult(
                matchesHl7Version('2.4'),
                'has a null orc_2_placer_order_number column'
            ).withAdditionalValidation(VALIDATION_ORC_PLACER_ORDER_NUM)),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F' AND ${COLUMN_RACE} IN ('BLACK', 'B')")
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.sex == Sex.FEMALE && radiologyReport.race == Race.BLACK
                }, "corresponds to a female patient with a pid_10_race value of 'B' or 'BLACK'"
            ).withAdditionalValidation(VALIDATION_RACE)),
        primaryModalityBySex()
    ]
    
    void processData(BatchSpecification batchSpecification) {
        queries.each { query ->
            query.updateExpectedResult(batchSpecification)
        }
    }

    List<TestQuery> getTestQueries() {
        queries
    }

    void writeQueries(File outputDir) {
        new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValue(
                new File(outputDir, 'queries.json'),
                queries
            )
    }

    private static TestQuery primaryModalityBySex() {
        new TestQuery(FileIOUtils.readResource('modality_query.sql'))
            .expecting(
                new GroupedAggregationResult(matchesHl7Version('2.7'))
                    .primaryColumn('primary_modality')
                    .primaryColumnDerivation({ report ->
                        ObrGenerator.derivePrimaryImagingModality(report.study)
                    }).addCase(
                        new GroupedAggregationResult.Case(
                            'male_count',
                            sexFilter(Sex.MALE)
                        )
                    ).addCase(
                        new GroupedAggregationResult.Case(
                            'female_count',
                                sexFilter(Sex.FEMALE)
                        )
                    ).addCase(
                        new GroupedAggregationResult.Case(
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
