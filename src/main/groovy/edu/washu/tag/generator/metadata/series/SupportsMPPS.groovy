package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.UID
import org.dcm4che3.data.VR

trait SupportsMPPS {

    String referencedPerformedProcedureStepSopInstanceUid

    void encodeMPPS(Attributes attributes) {
        if (referencedPerformedProcedureStepSopInstanceUid != null) {
            final Attributes sequenceItem = new Attributes()
            sequenceItem.setString(Tag.ReferencedSOPClassUID, VR.UI, UID.ModalityPerformedProcedureStep)
            sequenceItem.setString(Tag.ReferencedSOPInstanceUID, VR.UI, referencedPerformedProcedureStepSopInstanceUid)
            attributes.newSequence(Tag.ReferencedPerformedProcedureStepSequence, 1) << sequenceItem
        }
    }

}