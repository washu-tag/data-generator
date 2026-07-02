package edu.washu.tag.generator.temporal.activity

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.OutputManager
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.model.BatchHandlerActivityInput
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class BatchHandlerActivityImpl implements BatchHandlerActivity {

    private static final Logger logger = Workflow.getLogger(BatchHandlerActivityImpl)

    @Override
    void formAndWriteBatch(BatchHandlerActivityInput batchHandlerActivityInput) {
        final String outputPath = batchHandlerActivityInput.datasetInput.outputFullPath()
        BatchProcessor.initDirs(outputPath)
        final OutputManager outputManager = new OutputManager(outputPath)
        CodeCache.initializeCache()
        logger.info("Generating batch ${batchHandlerActivityInput.batchRequest.id}...")
        final PopulationGenerator populationGenerator = new PopulationGenerator()
        populationGenerator.readSpecificationParameters(
            batchHandlerActivityInput.datasetInput.specificationParametersPath
        )
        populationGenerator.generateAndWriteFullBatch(
            outputManager.readGenerationCache(),
            outputManager.readFixedOffsets(),
            batchHandlerActivityInput.batchRequest,
            batchHandlerActivityInput.datasetInput.writeDicom,
            batchHandlerActivityInput.datasetInput.writeHl7,
            true
        )

        logger.info("Fulfilled entire batch ${batchHandlerActivityInput.batchRequest.id}...")
    }

}
