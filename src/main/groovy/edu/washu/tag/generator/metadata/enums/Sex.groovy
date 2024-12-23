package edu.washu.tag.generator.metadata.enums

import ca.uhn.hl7v2.model.v281.datatype.CWE

enum Sex {

    MALE ('M'),
    FEMALE ('F')

    final String dicomRepresentation

    Sex(String representation) {
        dicomRepresentation = representation
    }

    @Override
    String toString() {
        dicomRepresentation
    }

    CWE toCwe(CWE emptyDataStore) {
        emptyDataStore.getCwe1_Identifier().setValue(dicomRepresentation)
        emptyDataStore
    }

}
