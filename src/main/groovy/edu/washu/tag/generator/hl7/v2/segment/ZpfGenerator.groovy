package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.AbstractGroup
import ca.uhn.hl7v2.model.GenericSegment
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.metadata.RadiologyReport

class ZpfGenerator extends NonstandardSegmentGenerator<GenericSegment> {

    @Override
    String getSegmentName() {
        'ZPF'
    }

    @Override
    AbstractGroup positionForSegment(ORU_R01 baseMessage) {
        baseMessage.PATIENT_RESULT.ORDER_OBSERVATION
    }

    @Override
    void generateSegment(RadiologyReport radReport, GenericSegment baseSegment) {
        Terser.set(baseSegment, 1, 0, 1, 1, '1')
        Terser.set(baseSegment, 2, 0, 1, 1, radReport.study.generalizedProcedure.code)
        Terser.set(baseSegment, 2, 0, 2, 1, radReport.study.generalizedProcedure.meaning)
    }

    @Override
    int insertionIndex(AbstractGroup position) {
        2
    }

}
