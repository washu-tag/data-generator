package edu.washu.tag.generator.query

import edu.washu.tag.QuerySourceDataProcessor
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.validation.ExpectedQueryResult

class FixedResultQueryProcessor implements QuerySourceDataProcessor<BatchSpecification> {

    ExpectedQueryResult expectedQueryResult

    FixedResultQueryProcessor(ExpectedQueryResult expectedQueryResult) {
        this.expectedQueryResult = expectedQueryResult
    }

    @Override
    void process(BatchSpecification batchSpecification) {

    }

    @Override
    ExpectedQueryResult outputExpectation() {
        expectedQueryResult
    }

}
