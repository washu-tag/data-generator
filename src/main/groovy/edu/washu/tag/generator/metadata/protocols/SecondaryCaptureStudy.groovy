package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.enums.BodyPart

abstract class SecondaryCaptureStudy extends Protocol {

    @Override
    List<BodyPart> getApplicableBodyParts() {
        null
    }

    @Override
    boolean includeMedicalStaff() {
        false
    }

    @Override
    boolean allowPatientPhysiqueEncoding() {
        false
    }

}
