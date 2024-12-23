package edu.washu.tag.generator.metadata.enums

enum XRayViewPosition {

    AP ('AP'),
    PA ('PA'),
    LL ('LL'),
    RL ('RL'),
    RLD ('RLD'),
    LLD ('LLD'),
    RLO ('RLO'),
    LLO ('LLO')

    final String dicomRepresentation

    XRayViewPosition(String dicomRepresentation) {
        this.dicomRepresentation = dicomRepresentation
    }

}