package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.model.GenerateBatchInput
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger

@ActivityImpl(taskQueues = TemporalApplication.CHILD_QUEUE)
class BatchHandlerActivityImpl implements BatchHandlerActivity {

    private static final Logger logger = Workflow.getLogger(BatchHandlerActivityImpl)

    @Override
    void formAndWriteBatch(GenerateBatchInput generateBatchInput) {
        logger.info("Generating batch ${generateBatchInput.batchRequest.id}...")
        final PopulationGenerator populationGenerator = new PopulationGenerator()
        populationGenerator.readSpecificationParameters(generateBatchInput.specificationParametersPath)
        final BatchSpecification batchSpec = populationGenerator.generateBatch(
            generateBatchInput.nameCache,
            generateBatchInput.idOffsets,
            generateBatchInput.batchRequest
        )

        logger.info("Generated batch file ${batchSpec.id}...")

        new BatchProcessor().writeSpec(batchSpec, batchSpec.id, 0)
    }

}
