package edu.washu.tag.generator.metadata.enums

/**
 * Defined terms from PS 3.3 C.8.9.1.1.3 Units
 */
enum PetUnit {

    CNTS,
    NONE,
    CM2,
    CM2ML,
    PCNT,
    CPS,
    BQML,
    MGMINML,
    UMOLMINML,
    MLMING,
    MLG,
    _1CM,
    UMOLML,
    PROPCNTS,
    PROPCPS,
    MLMINML,
    MLML,
    GML,
    STDDEV

    String getDicomRepresentation() {
        name().replace('_', '')
    }

}
