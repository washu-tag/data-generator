package edu.washu.tag.generator.temporal.model

import edu.washu.tag.generator.BatchSpecification

import java.nio.file.Paths

class GenerateDatasetInput {

    String specificationParametersPath
    Boolean writeDicom = true
    Boolean writeHl7 = true
    String outputDir
    int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS

    String outputFullPath() {
        Paths.get('/output', outputDir).toString()
    }

}
