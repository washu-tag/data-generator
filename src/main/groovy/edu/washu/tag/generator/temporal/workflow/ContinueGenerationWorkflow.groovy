package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.temporal.model.ContinueGenerationWorkflowInput
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface ContinueGenerationWorkflow {

    @WorkflowMethod
    void continueGeneration(ContinueGenerationWorkflowInput continueGenerationWorkflowInput)

}
