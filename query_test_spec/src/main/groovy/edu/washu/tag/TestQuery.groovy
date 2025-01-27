package edu.washu.tag

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.validation.ExpectedQueryResult

class TestQuery<X extends QuerySourceData> {

    ExpectedQueryResult expectedQueryResult
    String sql
    String id
    @JsonIgnore QuerySourceDataProcessor<X> querySourceDataProcessor

    TestQuery(String sql) {
        this.sql = sql
    }

    TestQuery() {
        
    }

    TestQuery<X> withDataProcessor(QuerySourceDataProcessor<X> querySourceDataProcessor) {
        this.querySourceDataProcessor = querySourceDataProcessor
        this
    }

}
