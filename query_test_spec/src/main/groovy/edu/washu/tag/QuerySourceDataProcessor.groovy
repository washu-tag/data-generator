package edu.washu.tag

import edu.washu.tag.validation.ExpectedQueryResult

interface QuerySourceDataProcessor<X extends QuerySourceData> {

    void process(X sourceData)

    ExpectedQueryResult outputExpectation()

}
