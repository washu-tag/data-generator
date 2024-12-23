package edu.washu.tag.generator.metadata.enums

enum NmScanType {

    STATIC,
    DYNAMIC,
    GATED,
    WHOLE_BODY,
    TOMO,
    GATED_TOMO,
    RECON_TOMO,
    RECON_GATED_TOMO

    String getDicomRepresentation() {
        name().replace('_', ' ')
    }

}