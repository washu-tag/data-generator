package edu.washu.tag.generator.hl7.v2.model

class DoctorEncoderLegacy extends DoctorEncoder2_7 {

    @Override
    protected boolean includeDegree() {
        true
    }

    @Override
    protected boolean includeAssigningAuthority() {
        false
    }

    @Override
    protected boolean includeIdentifierTypeCode() {
        false
    }

}
