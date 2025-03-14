package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.temporal.model.BatchChunk
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface EarlySetupHandlerActivity {

    @ActivityMethod
    List<BatchChunk> chunkBatches(String specificationParamsPath, int concurrentExecution)

    @ActivityMethod
    File initNameCache()

}
