package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.YamlObjectMapper
import edu.washu.tag.generator.metadata.NameCache
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger

@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class GenerateBatchActivityImpl implements GenerateBatchActivity {

    private static final Logger logger = Workflow.getLogger(GenerateBatchActivityImpl)

    @Override
    void generateBatch(String specificationParametersPath, NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest) {
        logger.info("Generating batch ${batchRequest.id}...")
        final BatchSpecification batchSpec = new PopulationGenerator(
            specificationParameters: new YamlObjectMapper().readValue(new File(specificationParametersPath), SpecificationParameters)
        ).generateBatch(nameCache, idOffsets, batchRequest)
        logger.info("Generated batch file ${batchSpec.id}...")

        new BatchProcessor().writeSpec(batchSpec, batchSpec.id, 0)
    }

}
