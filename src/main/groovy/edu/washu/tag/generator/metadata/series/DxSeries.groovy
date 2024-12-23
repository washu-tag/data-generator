package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.PresentationIntentType

class DxSeries extends Series implements SupportsMPPS {

    PresentationIntentType presentationIntentType

    @Override
    void encode(Attributes attributes) {
        super.encode(attributes)
        encodeMPPS(attributes)
        attributes.setString(Tag.PresentationIntentType, VR.CS, presentationIntentType.dicomRepresentation)
    }

}
