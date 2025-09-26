package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.enums.Sex

abstract class Mammogram extends Protocol {

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.BREAST]
    }

    @Override
    boolean isApplicableFor(Patient patient) {
        patient.sex == Sex.FEMALE
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.MAMMO
    }

}
