package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.BatchRequest
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface EarlySetupHandlerActivity {

    @ActivityMethod
    List<BatchRequest> resolveBatches(String specificationParamsPath, String outputDir, int patientsPerFullBatch)

    @ActivityMethod
    File initGenerationCache(String specificationParamsPath, String outputPath)

}
