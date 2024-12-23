package edu.washu.tag.generator.metadata.enums

enum PetSeriesTypeValue1 {

    STATIC,
    DYNAMIC,
    GATED,
    WHOLE_BODY

    String getDicomRepresentation() {
        name().replace('_', ' ')
    }

}