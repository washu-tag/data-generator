package edu.washu.tag.generator.temporal.model

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.BatchRequest

class BatchChunk {

    List<BatchRequest> batchRequests

    @JsonIgnore
    String workflowSubid() {
        if (batchRequests.size() == 1) {
            "batch-${batchRequests.first.id}"
        } else {
            "batches-${batchRequests.first.id}-${batchRequests.last.id}"
        }
    }

}
