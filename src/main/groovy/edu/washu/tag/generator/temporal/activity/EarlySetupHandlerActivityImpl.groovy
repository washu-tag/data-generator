package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger

@ActivityImpl(taskQueues = TemporalApplication.PARENT_QUEUE)
class EarlySetupHandlerActivityImpl implements EarlySetupHandlerActivity {

    private static final Logger logger = Workflow.getLogger(EarlySetupHandlerActivityImpl)

    @Override
    List<BatchRequest> chunkBatches(String specificationParamsPath) {
        BatchProcessor.initDirs()
        final PopulationGenerator generator = new PopulationGenerator()
        generator.readSpecificationParameters(specificationParamsPath)

        final List<BatchRequest> batchRequests = generator.chunkRequest()
        logger.info("Request has been split into ${batchRequests.size()} batches")
        batchRequests
    }

    @Override
    NameCache initNameCache() {
        NameCache.initInstance()
    }

}
