package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.temporal.model.GenerateBatchInput
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface GenerateBatchWorkflow {

    @WorkflowMethod
    void generateBatch(GenerateBatchInput generateBatchInput)

}
