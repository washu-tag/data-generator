package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.OutputManager

class GenerateDatasetInput {

    String specificationParametersPath
    Boolean writeDicom = true
    Boolean writeHl7 = true
    String outputDir
    int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS

    String outputFullPath() {
        OutputManager.prefixOutput(outputDir)
    }

}
