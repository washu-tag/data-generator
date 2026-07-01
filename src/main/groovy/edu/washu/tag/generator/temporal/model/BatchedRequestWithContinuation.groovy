package edu.washu.tag.generator.temporal.model

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.Continuation

class BatchedRequestWithContinuation {

    List<BatchRequest> batches
    Continuation continuation

    @JsonIgnore
    int size() {
        batches.size()
    }

}
