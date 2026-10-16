package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.Series

class CtSeries extends Series implements SupportsImagePlane {

    String convolutionKernel

    @Override
    void encode(Attributes attributes) {
        super.encode(attributes)
        encodeImagePlane(attributes)
        setIfNonnull(attributes, Tag.ConvolutionKernel, VR.SH, convolutionKernel)
    }

}
