package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.temporal.model.BatchedRequestWithContinuation
import edu.washu.tag.generator.temporal.model.ContinueGenerationWorkflowInput
import edu.washu.tag.generator.temporal.model.ExtendSpecWorkflowInput
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface EarlySetupHandlerActivity {

    @ActivityMethod
    BatchedRequestWithContinuation resolveBatches(String specificationParamsPath, String outputDir, int patientsPerFullBatch)

    @ActivityMethod
    BatchedRequestWithContinuation resolveBatchesForExtendedSpec(ExtendSpecWorkflowInput extendSpecWorkflowInput)

    @ActivityMethod
    BatchedRequestWithContinuation resolveBatchesForContinuation(ContinueGenerationWorkflowInput continueGenerationWorkflowInput)

    @ActivityMethod
    void initGenerationCache(String specificationParamsPath, String outputPath)

}
