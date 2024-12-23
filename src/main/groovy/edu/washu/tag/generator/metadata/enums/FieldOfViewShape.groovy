package edu.washu.tag.generator.metadata.enums

enum FieldOfViewShape {

    CYLINDRICAL_RING,
    HEXAGONAL,
    MULTIPLE_PLANAR

    String getDicomRepresentation() {
        name().replace('_', ' ')
    }

}