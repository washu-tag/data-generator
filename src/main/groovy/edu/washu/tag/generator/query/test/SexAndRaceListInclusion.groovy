package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.query.ExactNumberRadReportResult
import edu.washu.tag.validation.FixedColumnsValidator

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_SEX
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME

class SexAndRaceListInclusion extends TestQuery<BatchSpecification> {

    private static final String COLUMN_RACE = 'race'

    SexAndRaceListInclusion() {
        super('sex_and_race_list_inclusion', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F' AND ${COLUMN_RACE} IN ('BLACK', 'B')")
        withDataProcessor(
            new ExactNumberRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.sex == Sex.FEMALE && radiologyReport.race == Race.BLACK && radiologyReport.hl7Version != ReportVersion.V2_3
                }
            ).withAdditionalValidation(
                new FixedColumnsValidator()
                    .validating(COLUMN_SEX, 'F')
                    .validating(COLUMN_RACE, ['B', 'BLACK'])
            )
        )
    }

}
