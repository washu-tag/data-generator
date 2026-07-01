package edu.washu.tag.generator

import edu.washu.tag.generator.temporal.model.BatchedRequestWithContinuation

import java.util.function.Function

class Batcher {

    SpecificationParameters specificationParameters
    int patientsPerFullBatch
    int patientOffset = 0
    int studyOffset = 0
    int idOffset = 0
    int continuationId = 0

    Batcher(SpecificationParameters specificationParameters, int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS) {
        new File('batches').mkdir() // while we're still in a single process
        this.specificationParameters = specificationParameters
        this.patientsPerFullBatch = patientsPerFullBatch
    }

    /**
     * Resolves the entire requested population into a flat list of standalone batches. Each {@link BatchRequest}
     * carries its own patient/study offsets, so it can be fulfilled independently by any worker with no
     * coordination between batches.
     */
    List<BatchRequest> resolveBatches() {
        final List<BatchRequest> allBatches = computeBatchesFrom(
            specificationParameters.numPatients,
            specificationParameters.numStudies,
            specificationParameters.numSeries,
            { Integer id -> "standard batch ${id}" }
        )

        patientOffset += specificationParameters.numPatients
        studyOffset += specificationParameters.numStudies
        idOffset += allBatches.size()
        specificationParameters.cohorts.each { cohort ->
            cohort.trajectory.each { studyReq ->
                if (studyReq.protocol == null) {
                    throw new UnsupportedOperationException('Each StudyRequest must specify a protocol.')
                }
                studyReq.protocol.postprocess(specificationParameters)
            }
            final int cohortPatients = cohort.numPatients
            final int cohortStudies = cohortPatients * cohort.trajectory.size()
            final List<BatchRequest> batchesForCohort = computeBatchesFrom(
                cohortPatients,
                cohortStudies,
                0, // obviously not true, but we don't need this
                { id -> "cohort ${cohort.name} batch ${id}" }
            )
            batchesForCohort.each { batch ->
                batch.setCohortName(cohort.name)
                allBatches << batch
            }
            patientOffset += cohortPatients
            studyOffset += cohortStudies
            idOffset += batchesForCohort.size()
        }
        allBatches
    }

    BatchedRequestWithContinuation resolveBatchesWithContinuation() {
        new BatchedRequestWithContinuation(
            batches: resolveBatches(),
            continuation: new Continuation(
                continuationId: continuationId,
                batchIdOffset: idOffset,
                patientOffset: patientOffset,
                studyOffset: studyOffset
            )
        )
    }

    private List<BatchRequest> computeBatchesFrom(int numPatients, int numStudies, int numSeries, Function<Integer, String> summarizer) {
        if (numPatients == 0) {
            return []
        }
        final int totalNumBatches = Math.ceilDiv(numPatients, patientsPerFullBatch)

        final int patientsInSmallerBatches = numPatients / totalNumBatches
        final int patientRemainder = numPatients % totalNumBatches
        final int studiesInSmallerBatch = numStudies / totalNumBatches
        final int studyRemainder = numStudies % totalNumBatches
        final int seriesInSmallerBatch = numSeries / totalNumBatches
        final int seriesRemainder = numSeries % totalNumBatches

        (0 ..< totalNumBatches).collect { batchId ->
            new BatchRequest(
                id: idOffset + batchId,
                numPatients: patientsInSmallerBatches + (batchId < patientRemainder ? 1 : 0),
                numStudies: studiesInSmallerBatch + (batchId < studyRemainder ? 1 : 0),
                numSeries: seriesInSmallerBatch + (batchId < seriesRemainder ? 1 : 0),
                patientOffset: patientOffset + (patientsInSmallerBatches * batchId) + Math.min(batchId, patientRemainder),
                studyOffset: studyOffset + (studiesInSmallerBatch * batchId) + Math.min(batchId, studyRemainder),
                temporalSummary: summarizer.apply(batchId)
            )
        }
    }

}
