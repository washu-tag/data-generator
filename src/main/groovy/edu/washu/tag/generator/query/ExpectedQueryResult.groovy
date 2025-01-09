package edu.washu.tag.generator.query

import edu.washu.tag.generator.BatchSpecification

interface ExpectedQueryResult {

    void update(BatchSpecification batchSpecification)

}