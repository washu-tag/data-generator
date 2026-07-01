package edu.washu.tag.generator

class Continuation {

    int continuationId = 0
    int batchIdOffset
    int patientOffset
    int studyOffset

    String specificationParamsFileName() {
        "specification_parameters_${continuationId}.yaml"
    }

}
