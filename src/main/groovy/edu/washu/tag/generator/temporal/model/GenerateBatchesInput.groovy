package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.IdOffsets
import groovy.transform.TupleConstructor

@TupleConstructor
class GenerateBatchesInput {

    GenerateDatasetInput datasetInput
    File nameCachePath
    IdOffsets idOffsets
    BatchChunk batchChunk
    String outputDir

}
