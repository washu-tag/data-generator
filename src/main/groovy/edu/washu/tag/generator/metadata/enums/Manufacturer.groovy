package edu.washu.tag.generator.metadata.enums

enum Manufacturer {

    ACUSON ('ACUSON'),
    ADAC ('ADAC'),
    GE ('GE MEDICAL SYSTEMS'),
    GE_HEALTHCARE ('GE Healthcare'),
    HOLOGIC ('HOLOGIC, Inc.'),
    LORAD ('LORAD'),
    LUMISYS ('LUMISYS'),
    PHILIPS ('Philips Medical Systems'),
    SIEMENS ('SIEMENS'),
    SIEMENS_LOWERCASE ('Siemens'),
    SWIFT_PACS ('SwiftPACS') // does not actually exist

    String dicomRepresentation

    Manufacturer(String representation) {
        dicomRepresentation = representation
    }

}