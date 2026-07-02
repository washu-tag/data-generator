package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchRequest
import groovy.transform.TupleConstructor

@TupleConstructor
class BatchHandlerActivityInput {

    GenerateDatasetInput datasetInput
    BatchRequest batchRequest

}
