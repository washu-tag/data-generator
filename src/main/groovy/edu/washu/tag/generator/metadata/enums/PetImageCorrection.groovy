package edu.washu.tag.generator.metadata.enums

enum PetImageCorrection {

    // Defined terms in PS 3.3 C.8.9.1 - PET Series Module
    DECY,
    ATTN,
    SCAT,
    DTIM,
    MOTN,
    PMOT,
    CLN,
    RAN,
    RADL,
    DCAL,
    NORM,

    // Terms in Siemens conformance statements
    PGC,
    _2SCAT,
    _3SCAT,
    RARC,
    AARC,
    FLEN,
    FORE,
    SSRB,
    SEG0,
    RANSM,
    RANSUB,
    UNTI,
    XYSM,
    ZSM,
    GAMMA,
    MLAAFOV

    String getDicomRepresentation() {
        name()
    }

}