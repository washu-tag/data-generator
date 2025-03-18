package edu.washu.tag.generator.temporal.workflow

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface CombineHl7Workflow {

    @WorkflowMethod
    void combineLogs(File hl7BaseDir)

}
