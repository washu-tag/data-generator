package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR

/**
 * Attributes from the Image Plane module. These are instance-level in DICOM, but are modeled as constant across a series.
 */
trait SupportsImagePlane {

    Double sliceThickness

    void encodeImagePlane(Attributes attributes) {
        if (sliceThickness != null) {
            attributes.setString(Tag.SliceThickness, VR.DS, String.valueOf(sliceThickness))
        }
    }

}
