package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.temporal.GenerateDatasetInput
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface GenerateDatasetWorkflow {

    @WorkflowMethod
    void generateDataset(GenerateDatasetInput input)

}
