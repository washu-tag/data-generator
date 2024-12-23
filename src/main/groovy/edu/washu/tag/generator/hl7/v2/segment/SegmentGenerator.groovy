package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.Segment
import edu.washu.tag.generator.metadata.RadiologyReport

abstract class SegmentGenerator<T extends Segment> {

    abstract void generateSegment(RadiologyReport radReport, T baseSegment)

}
