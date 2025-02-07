package edu.washu.tag.generator.metadata.enums

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * See DICOM PS3.16 Annex L
 */
enum BodyPart {

    ABDOMEN ('ABDOMEN', 'T-D4000', 'SRT', 'Abdomen'),
    BRAIN ('BRAIN', 'T-A0100', 'SRT', 'Brain'),
    BREAST ('BREAST', 'T-04000', 'SRT', 'Breast'),
    CERVICAL_SPINE ('CSPINE', 'T-11501', 'SRT', 'Cervical spine'),
    CHEST ('CHEST', 'T-D3000', 'SRT', 'Chest'),
    COLON ('COLON', 'T-59300', 'SRT', 'Colon'),
    FOOT ('FOOT', 'T-D9700', 'SRT', 'Foot'),
    HEAD ('HEAD', 'T-D1100', 'SRT', 'Head'),
    HEADNECK ('HEADNECK', 'T-D1000', 'SRT', 'Head and Neck'),
    HEART ('HEART', 'T-32000', 'SRT', 'Heart'),
    HIP ('HIP', 'T-15710', 'SRT', 'Hip joint'),
    LEG ('LEG', 'T-D9400', 'SRT', 'Lower leg'),
    LIVER ('LIVER', 'T-62000', 'SRT', 'Liver'),
    LUNG ('LUNG', 'T-28000', 'SRT', 'Lung'),
    NECK ('NECK', 'T-D1600', 'SRT', 'Neck'),
    PANCREAS ('PANCREAS', 'T-65000', 'SRT', 'Pancreas'),
    PELVIS ('PELVIS', 'T-D6000', 'SRT', 'Pelvis'),
    PROSTATE ('PROSTATE', 'T-92000', 'SRT', 'Prostate'),
    SKULL ('SKULL', 'T-11100', 'SRT', 'Skull'),
    SPINE ('SPINE', 'T-D04FF', 'SRT', 'Spine'),
    THORACIC_SPINE ('TSPINE', 'T-11502', 'SRT', 'Thoracic spine'),
    THORAX ('THORAX', 'T-D3000', 'SRT', 'Thorax'),
    WHOLEBODY ('WHOLEBODY', 'T-D0010', 'SRT', 'Entire body'),
    UNKNOWN ('', '', '', '')

    String dicomRepresentation
    String codeMeaning
    CodedTriplet code

    BodyPart(String representation, String codeValue, String codingSchemeDesignator, String codeMeaning) {
        dicomRepresentation = representation
        this.codeMeaning = codeMeaning
        code = new CodedTriplet(codeValue, codingSchemeDesignator, codeMeaning)
    }

}
