package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.BatchProcessor
import edu.washu.tag.generator.Batcher
import edu.washu.tag.generator.Continuation
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.OutputManager
import edu.washu.tag.generator.PopulationGenerator
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.YamlObjectMapper
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.temporal.TemporalApplication
import edu.washu.tag.generator.temporal.model.BatchedRequestWithContinuation
import edu.washu.tag.generator.temporal.model.ContinueGenerationWorkflowInput
import edu.washu.tag.generator.temporal.model.ExtendSpecWorkflowInput
import edu.washu.tag.generator.temporal.model.GenerateDatasetInput
import io.temporal.failure.ApplicationFailure
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class EarlySetupHandlerActivityImpl implements EarlySetupHandlerActivity {

    private static final Logger logger = Workflow.getLogger(EarlySetupHandlerActivityImpl)

    @Override
    BatchedRequestWithContinuation resolveBatches(String specificationParamsPath, String outputDir, int patientsPerFullBatch) {
        BatchProcessor.initDirs(outputDir)

        final PopulationGenerator generator = new PopulationGenerator()
        generator.readSpecificationParameters(specificationParamsPath)

        final BatchedRequestWithContinuation batchRequests = new Batcher(generator.specificationParameters, patientsPerFullBatch).resolveBatchesWithContinuation()
        logger.info("Request has been resolved into ${batchRequests.size()} standalone batches")

        batchRequests
    }

    @Override
    BatchedRequestWithContinuation resolveBatchesForExtendedSpec(ExtendSpecWorkflowInput extendSpecWorkflowInput) {
        final String outputDir = OutputManager.prefixOutput(extendSpecWorkflowInput.previousDataset)
        BatchProcessor.initDirs(outputDir)
        final OutputManager outputManager = new OutputManager(outputDir)

        final SpecificationParameters existingSpec = outputManager.readSpecificationParametersFromOutputDir()
        existingSpec.setNumPatients(extendSpecWorkflowInput.newPatients)
        existingSpec.setNumStudies(extendSpecWorkflowInput.newStudies)
        existingSpec.setNumSeries(extendSpecWorkflowInput.newSeries)
        existingSpec.cohorts.each { cohort ->
            if (extendSpecWorkflowInput.existingCohortAdditions.containsKey(cohort.name)) {
                cohort.setNumPatients(extendSpecWorkflowInput.existingCohortAdditions[cohort.name])
            } else {
                logger.warn("Existing cohort ${cohort.name} was not found in extension object, falling back to original patient count of ${cohort.numPatients}")
            }
        }
        final Set<String> unknownCohorts = extendSpecWorkflowInput.existingCohortAdditions.keySet().findAll {
            !existingSpec.cohorts*.name.contains(it)
        }
        if (unknownCohorts.size() > 0) {
            throw ApplicationFailure.newNonRetryableFailure("Cohorts ${unknownCohorts} not found in existing specification parameters", 'incorrect-data')
        }

        final BatchedRequestWithContinuation batchRequests = outputManager.initBatcherFromLatestContinuation(
            existingSpec,
            extendSpecWorkflowInput.patientsPerFullBatch
        ).resolveBatchesWithContinuation()

        logger.info("Extension request has been resolved into ${batchRequests.size()} standalone batches")

        // The effective spec must be persisted now because every batch reads it during generation. The continuation
        // cursor, by contrast, is only written once the whole run succeeds (see persistContinuation), so a failed run
        // leaves the latest cursor unadvanced and a retry deterministically regenerates the same offsets/batch ids.
        outputManager.writeContinuationSpecificationParameters(existingSpec, batchRequests.continuation)

        batchRequests
    }

    @Override
    BatchedRequestWithContinuation resolveBatchesForContinuation(ContinueGenerationWorkflowInput continueGenerationWorkflowInput) {
        final String outputDir = OutputManager.prefixOutput(continueGenerationWorkflowInput.previousDataset)
        BatchProcessor.initDirs(outputDir)
        final OutputManager outputManager = new OutputManager(outputDir)

        final SpecificationParameters newSpec =
            new YamlObjectMapper().readValue(new File(continueGenerationWorkflowInput.newSpecificationPath), SpecificationParameters)
        final BatchedRequestWithContinuation batchRequests = outputManager.initBatcherFromLatestContinuation(
            newSpec,
            continueGenerationWorkflowInput.patientsPerFullBatch
        ).resolveBatchesWithContinuation()

        logger.info("Continuation request has been resolved into ${batchRequests.size()} standalone batches")

        outputManager.writeContinuationSpecificationParameters(newSpec, batchRequests.continuation)

        batchRequests
    }

    @Override
    void setupNewDataset(GenerateDatasetInput input) {
        final String outputPath = input.outputFullPath()
        final OutputManager outputManager = new OutputManager(outputPath)
        outputManager.ensureNonempty()
        BatchProcessor.initDirs(outputPath)
        outputManager.copySpecificationParameters(input.specificationParametersPath)

        final PopulationGenerator generator = new PopulationGenerator()
        generator.readSpecificationParameters(input.specificationParametersPath)
        outputManager.writeGenerationCacheToFile(GenerationCache.initInstance(generator.specificationParameters))
        outputManager.writeFixedOffsets(new IdOffsets())
    }

    @Override
    void persistContinuation(String outputDir, Continuation continuation) {
        new OutputManager(outputDir).writeContinuationToOutput(continuation)
    }

}
