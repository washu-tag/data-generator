package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import edu.washu.tag.generator.metadata.Series

class SrSeries extends Series implements SupportsMPPS {

    @Override
    void encode(Attributes attributes) {
        super.encode(attributes)
        if (referencedPerformedProcedureStepSopInstanceUid != null) {
            encodeMPPS(attributes)
        } else {
            attributes.newSequence(Tag.ReferencedPerformedProcedureStepSequence, 0) // specified as type 2 in SR Series Module
        }
    }

}
