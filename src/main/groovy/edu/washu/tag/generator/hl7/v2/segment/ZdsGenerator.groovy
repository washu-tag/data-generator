package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.AbstractGroup
import ca.uhn.hl7v2.model.GenericSegment
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.metadata.RadiologyReport

class ZdsGenerator extends NonstandardSegmentGenerator<GenericSegment> {

    @Override
    String getSegmentName() {
        'ZDS'
    }

    @Override
    void generateSegment(RadiologyReport radReport, GenericSegment baseSegment) {
        Terser.set(baseSegment, 1, 0, 1, 1, radReport.study.studyInstanceUid)
        Terser.set(baseSegment, 1, 0, 2, 1, 'EPIC')
        Terser.set(baseSegment, 1, 0, 3, 1, 'APPLICATION')
        Terser.set(baseSegment, 1, 0, 4, 1, 'DICOM')
    }

    @Override
    AbstractGroup positionForSegment(ORU_R01 baseMessage) {
        baseMessage.PATIENT_RESULT.ORDER_OBSERVATION
    }

}
