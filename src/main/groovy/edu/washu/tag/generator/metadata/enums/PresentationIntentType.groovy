package edu.washu.tag.generator.metadata.enums

enum PresentationIntentType {

    FOR_PRESENTATION ('FOR PRESENTATION'),
    FOR_PROCESSING ('FOR PROCESSING')

    final String dicomRepresentation

    PresentationIntentType(String dicomRepresentation) {
        this.dicomRepresentation = dicomRepresentation
    }

}