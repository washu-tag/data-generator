package edu.washu.tag

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.validation.ExpectedQueryResult

import java.util.function.Consumer

class TestQuery<X extends QuerySourceData> {

    ExpectedQueryResult expectedQueryResult
    String sql
    String id
    @JsonIgnore QuerySourceDataProcessor<X> querySourceDataProcessor
    @JsonIgnore Consumer<TestQuery<X>> postProcessing

    TestQuery(String id, String sql) {
        this.id = id
        this.sql = sql
    }

    TestQuery() {
        
    }

    TestQuery<X> withDataProcessor(QuerySourceDataProcessor<X> querySourceDataProcessor) {
        this.querySourceDataProcessor = querySourceDataProcessor
        this
    }

    TestQuery<X> withPostProcessing(Consumer<TestQuery<X>> postProcessing) {
        this.postProcessing = postProcessing
        this
    }

}
