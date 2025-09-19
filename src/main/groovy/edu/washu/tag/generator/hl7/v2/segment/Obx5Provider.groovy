package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.segment.OBX

abstract class Obx5Provider {

    abstract String getValueType(OBX baseSegment)

    abstract void encodeObx5(OBX baseSegment)

}
