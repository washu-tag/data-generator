package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.query.ExactNumberRadReportResult
import edu.washu.tag.validation.FixedColumnsValidator

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_SEX
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME
import static edu.washu.tag.generator.query.QueryUtils.sexFilter

class SexEquals extends TestQuery<BatchSpecification> {

    SexEquals() {
        super('sex_equals', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_SEX}='F'")
        withDataProcessor(
            new ExactNumberRadReportResult(sexFilter(Sex.FEMALE))
                .withAdditionalValidation(new FixedColumnsValidator(COLUMN_SEX, 'F'))
        )
    }

}
