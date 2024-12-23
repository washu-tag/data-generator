package edu.washu.tag.generator.metadata.enums

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * See DICOM PS3.16 Annex L
 */
enum BodyPart {

    ABDOMEN ('ABDOMEN', 'T-D4000', 'SRT', 'Abdomen', 0),
    BRAIN ('BRAIN', 'T-A0100', 'SRT', 'Brain', 1),
    BREAST ('BREAST', 'T-04000', 'SRT', 'Breast'),
    CERVICAL_SPINE ('CSPINE', 'T-11501', 'SRT', 'Cervical spine', 8),
    CHEST ('CHEST', 'T-D3000', 'SRT', 'Chest', 2),
    COLON ('COLON', 'T-59300', 'SRT', 'Colon'),
    FOOT ('FOOT', 'T-D9700', 'SRT', 'Foot'),
    HEAD ('HEAD', 'T-D1100', 'SRT', 'Head', 3),
    HEADNECK ('HEADNECK', 'T-D1000', 'SRT', 'Head and Neck'),
    HEART ('HEART', 'T-32000', 'SRT', 'Heart', 4),
    HIP ('HIP', 'T-15710', 'SRT', 'Hip joint'),
    LEG ('LEG', 'T-D9400', 'SRT', 'Lower leg'),
    LIVER ('LIVER', 'T-62000', 'SRT', 'Liver'),
    LUNG ('LUNG', 'T-28000', 'SRT', 'Lung'),
    NECK ('NECK', 'T-D1600', 'SRT', 'Neck'),
    PANCREAS ('PANCREAS', 'T-65000', 'SRT', 'Pancreas'),
    PELVIS ('PELVIS', 'T-D6000', 'SRT', 'Pelvis', 7),
    PROSTATE ('PROSTATE', 'T-92000', 'SRT', 'Prostate'),
    SKULL ('SKULL', 'T-11100', 'SRT', 'Skull', 9),
    SPINE ('SPINE', 'T-D04FF', 'SRT', 'Spine', 10),
    THORACIC_SPINE ('TSPINE', 'T-11502', 'SRT', 'Thoracic spine', 11),
    THORAX ('THORAX', 'T-D3000', 'SRT', 'Thorax', 5),
    WHOLEBODY ('WHOLEBODY', 'T-D0010', 'SRT', 'Entire body', 6),
    UNKNOWN ('', '', '', '')

    String dicomRepresentation
    String codeMeaning
    CodedTriplet code
    Integer procedureCodeOffset

    Integer offsetProcedureCode(int codeBase) {
        if (procedureCodeOffset == null) {
            throw new RuntimeException("BodyPart enum constant ${this} does not support procedure codes")
        }
        codeBase + procedureCodeOffset
    }

    BodyPart(String representation, String codeValue, String codingSchemeDesignator, String codeMeaning, Integer procedureCodeOffset = null) {
        dicomRepresentation = representation
        this.codeMeaning = codeMeaning
        code = new CodedTriplet(codeValue, codingSchemeDesignator, codeMeaning)
        this.procedureCodeOffset = procedureCodeOffset
    }

}
