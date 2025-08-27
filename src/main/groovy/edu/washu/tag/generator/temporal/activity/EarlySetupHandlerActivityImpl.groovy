package edu.washu.tag.generator.temporal.activity

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.model.BatchChunk
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class EarlySetupHandlerActivityImpl implements EarlySetupHandlerActivity {

    private static final Logger logger = Workflow.getLogger(EarlySetupHandlerActivityImpl)

    @Override
    List<BatchChunk> chunkBatches(String specificationParamsPath, int concurrentExecution, String outputDir, int patientsPerFullBatch) {
        BatchProcessor.initDirs(outputDir)
        final PopulationGenerator generator = new PopulationGenerator()
        generator.readSpecificationParameters(specificationParamsPath)

        final List<BatchRequest> batchRequests = generator.chunkRequest(patientsPerFullBatch)
        logger.info("Request has been split into ${batchRequests.size()} batches")

        batchRequests.collate(Math.ceilDiv(batchRequests.size(), concurrentExecution)).collect { batches ->
            new BatchChunk(batchRequests: batches)
        }
    }

    @Override
    File initNameCache() {
        final File asFile = new File("name_cache_${System.currentTimeMillis()}.json")
        new ObjectMapper().writeValue(asFile, NameCache.initInstance())
        asFile
    }

}
