package edu.washu.tag.generator.query

import edu.washu.tag.generator.BatchSpecification

class TestQuery {

    ExpectedQueryResult expectedQueryResult
    String sql

    TestQuery(String sql) {
        this.sql = sql
    }

    void updateExpectedResult(BatchSpecification batch) {
        expectedQueryResult.update(batch)
    }

    TestQuery expecting(ExpectedQueryResult result) {
        setExpectedQueryResult(result)
        this
    }

}
