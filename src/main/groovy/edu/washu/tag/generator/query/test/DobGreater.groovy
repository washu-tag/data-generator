package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.ExactNumberRadReportResult
import edu.washu.tag.validation.DateComparisonValidation
import java.time.LocalDate

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_DOB
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME

class DobGreater extends TestQuery<BatchSpecification> {

    DobGreater() {
        super('dob_greater', "SELECT * FROM ${TABLE_NAME} WHERE YEAR(${COLUMN_DOB}) > 1990")
        withDataProcessor(
            new ExactNumberRadReportResult(
                { RadiologyReport radiologyReport ->
                    radiologyReport.patient.dateOfBirth.isAfter(LocalDate.of(1990, 12, 31))
                }
            ).withAdditionalValidation(
                new DateComparisonValidation()
                    .description('patient born after 1990-12-31')
                    .columnName(COLUMN_DOB)
                    .comparisonValue(19901231)
                    .comparisonOperator(DateComparisonValidation.ComparisonOperator.GT)
            )
        )
    }

}
