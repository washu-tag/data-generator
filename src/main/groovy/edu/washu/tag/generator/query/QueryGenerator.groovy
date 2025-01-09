package edu.washu.tag.generator.query

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Sex

import java.time.LocalDate

class QueryGenerator {

    private static final String TABLE_NAME = 'syntheticdata'

    List<TestQuery> queries = [
        new TestQuery("SELECT COUNT(*) FROM ${TABLE_NAME} WHERE pid_8_administrative_sex='F'")
            .expecting(new ExactNumberRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.sex == Sex.FEMALE
                }
            )),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE zds_1_study_instance_uid='None'") // TODO: 'None'? Huh?
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.hl7Version == '2.4'
                }, 'has a null zds_1_study_instance_uid column'
            )),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE SUBSTRING(pid_7_date_time_of_birth, 1, 8) > '19901231'")
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.dateOfBirth.isAfter(LocalDate.of(1990, 12, 31))
                }, 'corresponds to a patient born after 1990-12-31'
            )),
        new TestQuery("SELECT * FROM ${TABLE_NAME} WHERE orc_2_placer_order_number='None'") // TODO: 'None'? Huh?
            .expecting(new ExactNumberDescriptionRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.hl7Version == '2.4'
                }, 'has a null orc_2_placer_order_number column'
            ))
    ]
    
    void processData(BatchSpecification batchSpecification) {
        queries.each { query ->
            query.updateExpectedResult(batchSpecification)
        }
    }

    void writeQueries(File outputDir) {
        new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValue(
                new File(outputDir, 'queries.json'),
                queries
            )
    }

}
