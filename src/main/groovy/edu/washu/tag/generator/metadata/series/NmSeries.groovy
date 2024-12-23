package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import edu.washu.tag.generator.metadata.Series

class NmSeries extends Series implements SupportsNmPetPatientOrientation {

    @Override
    void encode(Attributes attributes) {
        super.encode(attributes)
        encodePatientOrientation(attributes)
    }

}
