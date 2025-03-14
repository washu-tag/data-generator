package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.IdOffsets
import groovy.transform.TupleConstructor

@TupleConstructor
class BatchHandlerActivityInput {

    GenerateDatasetInput datasetInput
    File nameCachePath
    IdOffsets idOffsets
    BatchRequest batchRequest

}
