package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.XRayViewPosition

class CrSeries extends Series {

    XRayViewPosition viewPosition
    String filterType
    String collimatorGridName
    List<String> focalSpots
    String plateType
    String phosphorType

    @Override
    void encode(Attributes attributes) {
        super.encode(attributes)
        setIfNonnull(attributes, Tag.ViewPosition, VR.CS, viewPosition?.dicomRepresentation)
        setIfNonnull(attributes, Tag.FilterType, VR.SH, filterType)
        setIfNonnull(attributes, Tag.CollimatorGridName, VR.SH, collimatorGridName)
        setIfNonempty(attributes, Tag.FocalSpots, VR.DS, focalSpots)
        setIfNonnull(attributes, Tag.PlateType, VR.SH, plateType)
        setIfNonnull(attributes, Tag.PhosphorType, VR.LO, phosphorType)
    }

}
