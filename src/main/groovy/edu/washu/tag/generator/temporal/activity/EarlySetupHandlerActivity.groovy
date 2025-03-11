package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.metadata.NameCache
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface EarlySetupHandlerActivity {

    @ActivityMethod
    List<BatchRequest> chunkBatches(String specificationParamsPath)

    @ActivityMethod
    NameCache initNameCache()

}
