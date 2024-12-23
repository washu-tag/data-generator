package edu.washu.tag.generator.metadata.enums

enum ReprojectionMethod {

    SUM,
    MAX_PIXEL

    String getDicomRepresentation() {
        name().replace('_', ' ')
    }

}