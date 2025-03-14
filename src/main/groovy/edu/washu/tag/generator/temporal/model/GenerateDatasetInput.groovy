package edu.washu.tag.generator.temporal.model

class GenerateDatasetInput {

    String specificationParametersPath
    Boolean writeDicom = true
    Boolean writeHl7 = true
    int concurrentExecution = 4

}
