package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.NameCache
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface GenerateBatchActivity {

    @WorkflowMethod
    void generateBatch(SpecificationParameters specificationParameters, NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest)

}
