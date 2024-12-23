package edu.washu.tag.generator.metadata.enums.contextGroups

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 21
 */
enum PatientEquipmentRelationship {

    OBLIQUE ('R-10516', 'oblique'),
    HEADFIRST ('F-10470', 'headfirst'),
    FEET_FIRST ('F-10480', 'feet-first'),
    TRANSVERSE ('R-10515', 'transverse'),
    LEFT_FIRST ('126830', 'left first'),
    RIGHT_FIRST ('126831', 'right first'),
    POSTERIOR_FIRST ('126832', 'posterior first'),
    ANTERIOR_FIRST ('126833', 'anterior first')

    final CodedTriplet codedValue

    PatientEquipmentRelationship(String codeValue, String codeMeaning) {
        codedValue = new CodedTriplet(codeValue, '99SDM', codeMeaning)
    }

}