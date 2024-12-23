package edu.washu.tag.generator.metadata.enums

enum ConversionType {

    DIGITIZED_VIDEO ('DV'),
    DIGITAL_INTERFACE ('DI'),
    DIGITIZED_FILM ('DF'),
    WORKSTATION ('WSD'),
    SCANNED_DOCUMENT ('SD'),
    SCANNED_IMAGE ('SI'),
    DRAWING ('DRW'),
    SYNTHETIC_IMAGE ('SYN')

    final String dicomRepresentation

    ConversionType(String representation) {
        dicomRepresentation = representation
    }

}