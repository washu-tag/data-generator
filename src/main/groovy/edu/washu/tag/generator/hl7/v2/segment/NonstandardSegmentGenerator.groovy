package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.AbstractGroup
import ca.uhn.hl7v2.model.GenericSegment
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import edu.washu.tag.generator.metadata.RadiologyReport

abstract class NonstandardSegmentGenerator extends SegmentGenerator<GenericSegment> {

    void generateNonstandardSegment(RadiologyReport radiologyReport, ORU_R01 message) {
        final AbstractGroup position = positionForSegment(message)
        final String segmentName = getSegmentName()
        position.addNonstandardSegment(segmentName, insertionIndex(position))
        generateSegment(
                radiologyReport,
                position.get(segmentName) as GenericSegment
        )
    }

    int insertionIndex(AbstractGroup position) {
        position.getNames().length
    }

    abstract String getSegmentName()

    abstract AbstractGroup positionForSegment(ORU_R01 baseMessage)

}