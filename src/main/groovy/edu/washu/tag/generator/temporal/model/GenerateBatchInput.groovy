package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.metadata.NameCache
import groovy.transform.TupleConstructor

@TupleConstructor
class GenerateBatchInput {

    String specificationParametersPath
    NameCache nameCache
    IdOffsets idOffsets
    BatchRequest batchRequest

}
