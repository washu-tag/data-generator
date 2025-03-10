package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.temporal.GenerateBatchOutput
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface GenerateBatchWorkflow {

    @WorkflowMethod
    GenerateBatchOutput generateBatch(String specificationParametersPath, NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest)

}
