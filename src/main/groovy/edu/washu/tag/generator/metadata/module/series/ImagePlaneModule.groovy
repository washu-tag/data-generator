package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.SupportsImagePlane

/**
 * Image Plane module (PS3.3 C.7.6.2). The module is image-level, but its values are chosen once per series.
 */
class ImagePlaneModule implements SeriesLevelModule<Series> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, Series series) {
        final SupportsImagePlane castedSeries = series as SupportsImagePlane
        castedSeries.setSliceThickness(series.seriesType.sampleSliceThickness(equipment))
    }

}
