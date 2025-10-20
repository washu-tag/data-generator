package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.query.ExactNumberRadReportResult
import edu.washu.tag.validation.FixedColumnsValidator

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_HL7_VERSION
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME
import static edu.washu.tag.generator.query.QueryUtils.doesNotMatchHl7Version

class UidMissing extends TestQuery<BatchSpecification> {

    private static final String COLUMN_STUDY_INSTANCE_UID = 'study_instance_uid'

    UidMissing() {
        super('uid_missing', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_STUDY_INSTANCE_UID} IS NULL")
        withDataProcessor(
            new ExactNumberRadReportResult(doesNotMatchHl7Version('2.7'))
                .withAdditionalValidation(
                    new FixedColumnsValidator()
                        .validating(COLUMN_STUDY_INSTANCE_UID, [null])
                        .validating(COLUMN_HL7_VERSION, ['2.3', '2.4'])
                )
        )
    }

}
