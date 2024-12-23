package edu.washu.tag.generator.metadata.enums.contextGroups

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 4014 [INCOMPLETE]
 */
enum MammographyView {

    ML   ('ML',   'R-10224', 'SNM3', 'medio-lateral'),
    MLO  ('MLO',  'R-10226', 'SNM3', 'medio-lateral oblique'),
    LM   ('LM',   'R-10228', 'SNM3', 'latero-medial'),
    LMO  ('LMO',  'R-10230', 'SNM3', 'latero-medial oblique'),
    CC   ('CC',   'R-10242', 'SNM3', 'cranio-caudal'),
    XCCM ('XCCM', 'R-1024B', 'SNM3', 'cranio-caudal exaggerated medially')

    MammographyView(String representation, String codeValue, String codingSchemeDesignator, String codeMeaning) {
        dicomRepresentation = representation
        if (codeValue != null) {
            code = new CodedTriplet(codeValue, codingSchemeDesignator, codeMeaning)
        } else {
            code = null
        }
    }

    final String dicomRepresentation
    final CodedTriplet code

}