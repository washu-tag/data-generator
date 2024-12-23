package edu.washu.tag.generator.metadata.sequence

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Sequence
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.DicomEncoder

class EnergyWindowRangeSequence extends ArrayList<Item> implements SequenceElement, DicomEncoder {

    @Override
    void addToAttributes(Attributes attributes) {
        if (!isEmpty()) {
            final Sequence sequence = attributes.newSequence(Tag.EnergyWindowRangeSequence, size())
            this.each { item ->
                final Attributes attributesItem = new Attributes()
                setIfNonnull(attributesItem, Tag.EnergyWindowLowerLimit, VR.DS, item.lowerLimit)
                setIfNonnull(attributesItem, Tag.EnergyWindowUpperLimit, VR.DS, item.upperLimit)
                sequence << attributesItem
            }
        }
    }

    static class Item {
        String lowerLimit
        String upperLimit
    }

}
