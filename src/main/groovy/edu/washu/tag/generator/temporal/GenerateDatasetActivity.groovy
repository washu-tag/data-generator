package edu.washu.tag.generator.temporal

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface GenerateDatasetActivity {

    @WorkflowMethod
    void generateDataset(String specificationParametersPath)

}
