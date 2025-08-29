package edu.washu.tag.generator

import com.fasterxml.jackson.annotation.JsonIgnore

import static java.lang.Math.round

class BatchChunk {

    int numPatients
    int numStudies
    int numSeries
    int patientOffset
    int studyOffset
    int batchIdOffset
    int patientsPerFullBatch

    @JsonIgnore
    int calculateNumBatches() {
        Math.ceilDiv(numPatients, patientsPerFullBatch)
    }

    @JsonIgnore
    List<BatchRequest> resolveToBatches() {
        final int patientsInIncompleteBatch = numPatients % patientsPerFullBatch
        final boolean hasIncompleteBatch = patientsInIncompleteBatch != 0
        final int studiesPerFullBatch = round(((long) patientsPerFullBatch * (long) numStudies) / numPatients).intValue()
        final int studiesInIncompleteBatch = numStudies % studiesPerFullBatch
        final int seriesPerFullBatch = round(((long) patientsPerFullBatch * (long) numSeries) / numPatients).intValue()
        final int seriesInIncompleteBatch = numSeries % seriesPerFullBatch
        final int totalNumBatches = calculateNumBatches()
        (0 ..< totalNumBatches).collect { batchId ->
            final boolean isPartialBatch = hasIncompleteBatch && batchId == totalNumBatches - 1
            new BatchRequest(
                id: batchIdOffset + batchId,
                numPatients: isPartialBatch ? patientsInIncompleteBatch : patientsPerFullBatch,
                numStudies: isPartialBatch ? studiesInIncompleteBatch : studiesPerFullBatch,
                numSeries:  isPartialBatch ? seriesInIncompleteBatch : seriesPerFullBatch,
                patientOffset: patientOffset + patientsPerFullBatch * batchId,
                studyOffset: studyOffset + studiesPerFullBatch * batchId
            )
        }
    }

    @JsonIgnore
    String workflowSubid() {
        final int numBatches = calculateNumBatches()
        if (numBatches == 1) {
            "batch-${batchIdOffset}"
        } else {
            "batches-${batchIdOffset}-${batchIdOffset + numBatches - 1}"
        }
    }

}
