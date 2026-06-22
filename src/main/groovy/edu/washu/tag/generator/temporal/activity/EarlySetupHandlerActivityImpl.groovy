package edu.washu.tag.generator.temporal.activity

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

import java.nio.file.Paths

@Component
@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class EarlySetupHandlerActivityImpl implements EarlySetupHandlerActivity {

    private static final Logger logger = Workflow.getLogger(EarlySetupHandlerActivityImpl)

    @Override
    List<BatchRequest> resolveBatches(String specificationParamsPath, String outputDir, int patientsPerFullBatch) {
        BatchProcessor.initDirs(outputDir)
        final PopulationGenerator generator = new PopulationGenerator()
        generator.readSpecificationParameters(specificationParamsPath)

        final List<BatchRequest> batchRequests = generator.resolveBatches(patientsPerFullBatch)
        logger.info("Request has been resolved into ${batchRequests.size()} standalone batches")

        batchRequests
    }

    @Override
    File initGenerationCache(String specificationParamsPath, String outputPath) {
        final PopulationGenerator generator = new PopulationGenerator()
        generator.readSpecificationParameters(specificationParamsPath)
        final File asFile = Paths.get(outputPath, "generation_cache_${System.currentTimeMillis()}.json").toFile()
        new ObjectMapper().writeValue(asFile, GenerationCache.initInstance(generator.specificationParameters))
        asFile
    }

}
