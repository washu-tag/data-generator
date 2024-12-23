package edu.washu.tag.generator.metadata.seriesTypes.xa

import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.XaImagingPlane

class XaImageType {

    boolean original = true
    boolean primary = true
    List<String> additionalValues = []

    XaImagingPlane imagingPlane = XaImagingPlane.SINGLE_PLANE

    XaImageType imagingPlane(XaImagingPlane plane) {
        setImagingPlane(plane)
        this
    }

    XaImageType addValue(String value) {
        additionalValues << value
        this
    }

    ImageType resolve() {
        new ImageType(original : original, primary : primary, otherValues : [imagingPlane.dicomRepresentation] + additionalValues)
    }

}
