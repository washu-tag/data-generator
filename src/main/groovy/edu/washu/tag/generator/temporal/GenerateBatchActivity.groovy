package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.metadata.NameCache
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface GenerateBatchActivity {

    @ActivityMethod
    void generateBatch(String specificationParametersPath, NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest)

}
