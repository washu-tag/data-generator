package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.ExactNumberRadReportResult

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_MESSAGE_DT
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME

class NullMessageDt extends TestQuery<BatchSpecification> {

    NullMessageDt() {
        super('null_message_dt', "SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_MESSAGE_DT} IS NULL")
        withDataProcessor(
            new ExactNumberRadReportResult({ RadiologyReport radiologyReport ->
                radiologyReport.reportDateTime == null
            })
        )
    }

}
