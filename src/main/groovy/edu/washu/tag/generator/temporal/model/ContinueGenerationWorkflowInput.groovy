package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchSpecification

class ContinueGenerationWorkflowInput {

    String previousDataset
    String newSpecificationPath
    boolean writeDicom = true
    boolean writeHl7 = true
    Integer patientsPerFullBatch = BatchSpecification.MAX_PATIENTS

}
