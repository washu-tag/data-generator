package edu.washu.tag.generator

import com.fasterxml.jackson.annotation.JsonIgnore

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
        final int totalNumBatches = calculateNumBatches()

        final int patientsInSmallerBatches = numPatients / totalNumBatches
        final int remainingPatientsToAllocate = numPatients % totalNumBatches
        final int studiesInSmallerBatch = numStudies / totalNumBatches
        final int remainingStudiesToAllocate = numStudies % totalNumBatches
        final int seriesInSmallerBatch = numSeries / totalNumBatches
        final int remainingSeriesToAllocate = numSeries % totalNumBatches

        (0 ..< totalNumBatches).collect { batchId ->
            new BatchRequest(
                id: batchIdOffset + batchId,
                numPatients: patientsInSmallerBatches + (batchId < remainingPatientsToAllocate ? 1 : 0),
                numStudies: studiesInSmallerBatch + (batchId < remainingStudiesToAllocate ? 1 : 0),
                numSeries: seriesInSmallerBatch + (batchId < remainingSeriesToAllocate ? 1 : 0),
                patientOffset: patientOffset + patientsInSmallerBatches * batchId + Math.min(batchId, remainingPatientsToAllocate),
                studyOffset: studyOffset + studiesInSmallerBatch * batchId + Math.min(batchId, remainingStudiesToAllocate)
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
