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
                { id -> "cohort ${cohort.name} batch ${id}" },
                cohort.trajectory.size()
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

    // perPatientStudyCount is only known for cohorts, where every patient produces exactly trajectory.size()
    // studies. When given, each batch's numStudies/studyOffset is derived from that batch's own numPatients
    // rather than independently proportioned from the total - otherwise a patient split that doesn't divide
    // evenly across batches (e.g. 17/17/16) can diverge from an independently-rounded study split (e.g.
    // 117/117/116 instead of the true 119/119/112), causing one batch's studyIdGenerator to run past its
    // allocation into the next batch's range and hand out duplicate studyIds - and therefore duplicate
    // AccessionNumbers - to two different patients. The non-cohort path has no fixed per-patient study count
    // to derive from (patients there can have a varying number of studies), so it keeps the proportional split.
    private List<BatchRequest> computeBatchesFrom(int numPatients, int numStudies, int numSeries, Function<Integer, String> summarizer, Integer perPatientStudyCount = null) {
        if (numPatients == 0) {
            return []
        }
        final int totalNumBatches = Math.ceilDiv(numPatients, patientsPerFullBatch)

        final int patientsInSmallerBatches = numPatients / totalNumBatches
        final int patientRemainder = numPatients % totalNumBatches
        final int seriesInSmallerBatch = numSeries / totalNumBatches
        final int seriesRemainder = numSeries % totalNumBatches

        if (perPatientStudyCount != null) {
            int cumulativeStudyOffset = studyOffset
            return (0 ..< totalNumBatches).collect { batchId ->
                final int batchPatients = patientsInSmallerBatches + (batchId < patientRemainder ? 1 : 0)
                final int batchStudies = batchPatients * perPatientStudyCount
                final BatchRequest batch = new BatchRequest(
                    id: idOffset + batchId,
                    numPatients: batchPatients,
                    numStudies: batchStudies,
                    numSeries: seriesInSmallerBatch + (batchId < seriesRemainder ? 1 : 0),
                    patientOffset: patientOffset + (patientsInSmallerBatches * batchId) + Math.min(batchId, patientRemainder),
                    studyOffset: cumulativeStudyOffset,
                    temporalSummary: summarizer.apply(batchId)
                )
                cumulativeStudyOffset += batchStudies
                batch
            }
        }

        final int studiesInSmallerBatch = numStudies / totalNumBatches
        final int studyRemainder = numStudies % totalNumBatches

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
