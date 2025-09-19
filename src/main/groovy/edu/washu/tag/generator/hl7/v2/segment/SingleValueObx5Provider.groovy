package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.v281.segment.OBX

abstract class SingleValueObx5Provider extends Obx5Provider {

    protected final String content
    private Type resolvedType

    SingleValueObx5Provider(String content) {
        this.content = content
    }

    private void ensureResolved(OBX baseSegment) {
        if (resolvedType == null) {
            resolvedType = resolveType(baseSegment)
        }
    }

    abstract Type resolveType(OBX baseSegment)

    @Override
    String getValueType(OBX baseSegment) {
        ensureResolved(baseSegment)
        resolvedType.class.simpleName
    }

    @Override
    void encodeObx5(OBX baseSegment) {
        baseSegment.getObx5_ObservationValue(0).setData(resolvedType)
    }

}