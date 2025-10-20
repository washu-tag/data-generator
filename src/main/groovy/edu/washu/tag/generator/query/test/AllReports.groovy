package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.query.ExactNumberRadReportResult

import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME

class AllReports extends TestQuery<BatchSpecification> {

    AllReports() {
        super('all', "SELECT * FROM ${TABLE_NAME}")
        withDataProcessor(
            new ExactNumberRadReportResult({ true })
        )
    }

}
