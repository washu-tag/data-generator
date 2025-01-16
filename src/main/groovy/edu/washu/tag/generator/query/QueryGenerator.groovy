package edu.washu.tag.generator.query

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.util.FileIOUtils

import java.time.LocalDate
import java.util.function.Function

class QueryGenerator {

    private static final String TABLE_NAME = 'syntheticdata'
    private static final String COLUMN_SEX = 'pid_8_administrative_sex'

    private final List<TestQuery> queries = [
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F'")
            .expecting(
                new ExactNumberRadReportResult(sexFilter(Sex.FEMALE))
                    .withAdditionalValidation({ row ->
                        assertEquals('F', row.getString(row.fieldIndex(COLUMN_SEX)))
                    })
            ),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE zds_1_study_instance_uid='None'") // TODO: 'None'? Huh?
            .expecting(new ExactNumberDescriptionRadReportResult(
                matchesHl7Version('2.4'),
                'has a null zds_1_study_instance_uid column'
            )),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE SUBSTRING(pid_7_date_time_of_birth, 1, 8) > '19901231'")
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.dateOfBirth.isAfter(LocalDate.of(1990, 12, 31))
                }, 'corresponds to a patient born after 1990-12-31'
            )),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE orc_2_placer_order_number='None'") // TODO: 'None'? Huh?
            .expecting(new ExactNumberDescriptionRadReportResult(
                matchesHl7Version('2.4'),
                'has a null orc_2_placer_order_number column'
            )),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE pid_8_administrative_sex='F' AND pid_10_race IN ('BLACK', 'B')")
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.sex == Sex.FEMALE && radiologyReport.race == Race.BLACK
                }, "corresponds to a female patient with a pid_10_race value of 'B' or 'BLACK'"
            )),
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
