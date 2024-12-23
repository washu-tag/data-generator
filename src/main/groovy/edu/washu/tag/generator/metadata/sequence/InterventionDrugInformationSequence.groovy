package edu.washu.tag.generator.metadata.sequence

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Sequence
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.DicomEncoder
import edu.washu.tag.generator.metadata.enums.contextGroups.InterventionalDrug

import java.time.LocalTime

class InterventionDrugInformationSequence extends ArrayList<Item> implements SequenceElement, DicomEncoder {

    @Override
    void addToAttributes(Attributes attributes) {
        if (!isEmpty()) {
            final Sequence sequence = attributes.newSequence(Tag.InterventionDrugInformationSequence, size())
            this.each { item ->
                sequence << item.toSequenceItem()
            }
        }
    }

    class Item {
        String interventionDrugName
        InterventionalDrug interventionDrugCode
        LocalTime interventionDrugStartTime
        LocalTime interventionDrugStopTime
        String interventionDrugDose

        Attributes toSequenceItem() {
            final Attributes attributes = new Attributes()
            setIfNonnull(attributes, Tag.InterventionDrugName, VR.LO, interventionDrugName)
            if (interventionDrugCode != null) {
                attributes.newSequence(Tag.InterventionDrugCodeSequence, 1) << interventionDrugCode.codedValue.toSequenceItem()
            }
            setTimeIfNonnull(attributes, Tag.InterventionDrugStartTime, interventionDrugStartTime)
            setTimeIfNonnull(attributes, Tag.InterventionDrugStopTime, interventionDrugStopTime)
            setIfNonnull(attributes, Tag.InterventionDrugDose, VR.DS, interventionDrugDose)
            attributes
        }
    }
}
