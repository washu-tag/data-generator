package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchSpecification

class GenerateDatasetInput {

    String specificationParametersPath
    Boolean writeDicom = true
    Boolean writeHl7 = true
    int concurrentExecution = 4
    String outputDir = '.'
    int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS

}
