package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchSpecification

class ExtendSpecWorkflowInput {

    String previousDataset
    Integer newPatients = 0
    Integer newStudies = 0
    Integer newSeries = 0
    Map<String, Integer> existingCohortAdditions = [:]
    boolean writeDicom = true
    boolean writeHl7 = true
    int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS

}
