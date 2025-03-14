package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.temporal.model.GenerateBatchesInput
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface GenerateBatchWorkflow {

    @WorkflowMethod
    void generateBatches(GenerateBatchesInput generateBatchInput)

}
