package edu.washu.tag.generator.hl7.v2.model

class DoctorEncoderLegacy extends DoctorEncoder2_7 {

    @Override
    boolean includeDegree() {
        true
    }

    @Override
    boolean includeAssigningAuthority() {
        false
    }

    @Override
    boolean includeIdentifierTypeCode() {
        false
    }

}
