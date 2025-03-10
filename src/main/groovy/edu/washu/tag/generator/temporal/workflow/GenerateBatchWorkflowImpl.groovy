package edu.washu.tag.generator.temporal.workflow

import edu.washu.tag.generator.*
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.temporal.GenerateBatchOutput
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger

@WorkflowImpl(taskQueues = TemporalApplication.CHILD_QUEUE)
class GenerateBatchWorkflowImpl implements GenerateBatchWorkflow {

    private static final Logger logger = Workflow.getLogger(GenerateBatchWorkflowImpl)

    @Override
    GenerateBatchOutput generateBatch(String specificationParametersPath, NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest) {
        logger.info("Generating batch ${batchRequest.id}...")
        final BatchSpecification batchSpec = new PopulationGenerator(
            specificationParameters: new YamlObjectMapper().readValue(new File(specificationParametersPath), SpecificationParameters)
        ).generateBatch(nameCache, idOffsets, batchRequest)
        logger.info("Generated batch file ${batchSpec.id}...")

        new BatchProcessor().writeSpec(batchSpec, batchSpec.id, 0)
        new GenerateBatchOutput()
    }

}
