package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.Hl7Logger
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.YamlObjectMapper
import edu.washu.tag.generator.metadata.NameCache
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.internal.sync.AllOfPromise
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Async
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInfo
import org.slf4j.Logger

import java.time.Duration

@WorkflowImpl(taskQueues = 'data-generator')
class GenerateDatasetActivityImpl implements GenerateDatasetActivity {

    private static final Logger logger = Workflow.getLogger(GenerateDatasetActivityImpl)
    private final GenerateBatchActivity generateProcessorActivity =
        Workflow.newActivityStub(
            GenerateBatchActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(60))
                .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumInterval(Duration.ofSeconds(1))
                    .setMaximumAttempts(3)
                    .build())
                .build()
        )
    private final FormHl7LogActivity formHl7LogActivity =
        Workflow.newActivityStub(
            FormHl7LogActivity,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumInterval(Duration.ofSeconds(1))
                    .setMaximumAttempts(3)
                    .build())
                .build()
        )

    @Override
    void generateDataset(String specificationParametersPath) {
        final WorkflowInfo workflowInfo = Workflow.getInfo()
        final String workflowLoggingInfo = "[workflowId: ${workflowInfo.workflowId}]"
        logger.info("${workflowLoggingInfo} Beginning workflow ${this.class.simpleName}")

        BatchProcessor.initDirs()
        final PopulationGenerator generator = new PopulationGenerator()

        final NameCache nameCache = NameCache.initInstance()
        final IdOffsets idOffsets = new IdOffsets()
        final SpecificationParameters specificationParameters = new YamlObjectMapper().readValue(
            new File(specificationParametersPath),
            SpecificationParameters
        )

        final List<BatchRequest> batchRequests = generator.chunkRequest()
        logger.info("${workflowLoggingInfo} Request has been split into ${batchRequests.size()} batches")

        Promise.allOf(batchRequests.collect { batchRequest ->
            Async.function(generateProcessorActivity.&generateBatch, specificationParameters, nameCache, idOffsets, batchRequest)
        }).get()

        logger.info("${workflowLoggingInfo} all batches have been written")

        final Hl7Logger hl7Logger = new Hl7Logger()
        Promise.allOf(hl7Logger.identifyHl7LogFiles(BatchProcessor.hl7Output).collect { hl7LogFile ->
            Async.function(formHl7LogActivity.&formLogFile, hl7LogFile)
        }).get()
    }

}
