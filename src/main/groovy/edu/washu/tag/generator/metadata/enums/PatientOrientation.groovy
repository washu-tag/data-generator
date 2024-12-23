package edu.washu.tag.generator.metadata.enums

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 19
 */
enum PatientOrientation {

    ERECT ('C86043', 'erect'),
    RECUMBENT ('F-10450', 'recumbent'),
    SEMI_ERECT ('F-10460', 'semi-erect')

    final CodedTriplet codedValue

    PatientOrientation(String codeValue, String codeMeaning) {
        codedValue = new CodedTriplet(codeValue, '99SDM', codeMeaning)
    }

}