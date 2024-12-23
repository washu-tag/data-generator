package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Sequence
import org.dcm4che3.data.Tag
import edu.washu.tag.generator.metadata.enums.contextGroups.PatientEquipmentRelationship
import edu.washu.tag.generator.metadata.enums.PatientOrientation
import edu.washu.tag.generator.metadata.enums.contextGroups.PatientOrientationModifier

trait SupportsNmPetPatientOrientation {

    PatientOrientation patientOrientationSeqValue
    PatientOrientationModifier patientOrientationModifierSeqValue
    PatientEquipmentRelationship patientGantryRelationshipSeqValue

    void encodePatientOrientation(Attributes attributes) {
        final Sequence patientOrientationCodeSequence = attributes.newSequence(Tag.PatientOrientationCodeSequence, 0)
        if (patientOrientationSeqValue != null) {
            final Attributes patientOrientationCodeSequenceItem = patientOrientationSeqValue.codedValue.toSequenceItem()
            if (patientOrientationModifierSeqValue != null) {
                patientOrientationCodeSequenceItem.newSequence(Tag.PatientOrientationModifierCodeSequence, 1) << patientOrientationModifierSeqValue.codedValue.toSequenceItem()
            }
            patientOrientationCodeSequence << patientOrientationCodeSequenceItem
        }
        final Sequence patientGantryRelationshipCodeSequence = attributes.newSequence(Tag.PatientGantryRelationshipCodeSequence, 0)
        if (patientGantryRelationshipSeqValue != null) {
            patientGantryRelationshipCodeSequence << patientGantryRelationshipSeqValue.codedValue.toSequenceItem()
        }
    }

}