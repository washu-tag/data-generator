package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.NameCache
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowMethod
import org.slf4j.Logger

@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class GenerateBatchActivityImpl implements GenerateBatchActivity {

    private static final Logger logger = Workflow.getLogger(GenerateBatchActivityImpl)

    @WorkflowMethod
    void generateBatch(SpecificationParameters specificationParameters, NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest) {
        logger.info("Generating batch ${batchRequest.id}...")
        final BatchSpecification batchSpec = new PopulationGenerator(
            specificationParameters: specificationParameters
        ).generateBatch(nameCache, idOffsets, batchRequest)
        logger.info("Generated batch file ${batchSpec.id}...")

        new BatchProcessor().writeSpec(batchSpec, batchSpec.id, 0)
    }

}
