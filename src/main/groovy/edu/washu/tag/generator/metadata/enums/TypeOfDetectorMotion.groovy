package edu.washu.tag.generator.metadata.enums

enum TypeOfDetectorMotion {

    NONE,
    STEP_AND_SHOOT,
    CONTINUOUS,
    WOBBLE,
    CLAMSHELL

    String getDicomRepresentation() {
        name().replace('_', ' ')
    }

}