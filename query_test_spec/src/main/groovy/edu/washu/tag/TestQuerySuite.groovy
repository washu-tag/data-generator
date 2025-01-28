package edu.washu.tag

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class TestQuerySuite<X extends QuerySourceData> {

    String viewName
    List<TestQuery<X>> testQueries

}