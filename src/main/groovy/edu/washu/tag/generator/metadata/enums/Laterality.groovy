package edu.washu.tag.generator.metadata.enums

enum Laterality {

    LEFT ('L'),
    RIGHT ('R')

    final String dicomRepresentation

    Laterality(String representation) {
        dicomRepresentation = representation
    }

}
