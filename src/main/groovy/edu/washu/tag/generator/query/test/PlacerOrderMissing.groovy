package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.query.ExactNumberRadReportResult
import edu.washu.tag.validation.FixedColumnsValidator

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_HL7_VERSION
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME
import static edu.washu.tag.generator.query.QueryUtils.matchesHl7Version

class PlacerOrderMissing extends TestQuery<BatchSpecification> {

    private static final String COLUMN_ORC_PLACER_ORDER_NUM = 'orc_2_placer_order_number'

    PlacerOrderMissing() {
        super('placer_order_missing', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_ORC_PLACER_ORDER_NUM} IS NULL")
        withDataProcessor(
            new ExactNumberRadReportResult(matchesHl7Version('2.4'))
                .withAdditionalValidation(
                    new FixedColumnsValidator()
                        .validating(COLUMN_ORC_PLACER_ORDER_NUM, [null])
                        .validating(COLUMN_HL7_VERSION, '2.4')
                )
        )
    }

}
