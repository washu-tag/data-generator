package edu.washu.tag.generator.metadata.enums.contextGroups

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 20
 */
enum PatientOrientationModifier {

    PRONE ('F-10310', 'prone'),
    SEMI_PRONE ('F-10316', 'semi-prone'),
    LATERAL_DECUBITUS ('F-10318', 'lateral decubitus'),
    STANDING ('F-10320', 'standing'),
    ANATOMICAL ('F-10326', 'anatomical'),
    KNEELING ('F-10330', 'kneeling'),
    KNEE_CHEST ('F-10336', 'knee-chest'),
    SUPINE ('F-10340', 'supine'),
    LITHOTOMY ('F-10346', 'lithotomy'),
    TRENDELENBURG ('F-10348', 'Trendelenburg'),
    INVERSE_TRENDELENBURG ('F-10349', 'inverse Trendelenburg'),
    FROG ('F-10380', 'frog'),
    STOOPED_OVER ('F-10390', 'stooped-over'),
    SITTING ('F-103A0', 'sitting'),
    CURLED_UP ('F-10410', 'curled-up'),
    RIGHT_LATERAL_DECUBITUS ('F-10317', 'right lateral decubitus'),
    LEFT_LATERAL_DECUBITUS ('F-10319', 'left lateral decubitus'),
    LORDOTIC ('R-40799', 'lordotic')

    final CodedTriplet codedValue

    PatientOrientationModifier(String codeValue, String codeMeaning) {
        codedValue = new CodedTriplet(codeValue, '99SDM', codeMeaning)
    }

}