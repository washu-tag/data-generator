package edu.washu.tag.generator.metadata.enums

enum XaImagingPlane {

    SINGLE_PLANE,
    BIPLANE_A,
    BIPLANE_B

    String getDicomRepresentation() {
        name().replace('_', ' ')
    }

}