package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.temporal.model.ExtendSpecWorkflowInput
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface ExtendSpecWorkflow {

    @WorkflowMethod
    void extendSpec(ExtendSpecWorkflowInput extendSpecWorkflowInput)

}
