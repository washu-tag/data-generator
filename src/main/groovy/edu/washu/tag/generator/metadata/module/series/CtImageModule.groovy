package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.CtSeries
import edu.washu.tag.generator.metadata.seriesTypes.ct.CtSeriesType

/**
 * CT Image module (PS3.3 C.8.2.1). The module is image-level, but its values are chosen once per series.
 */
class CtImageModule implements SeriesLevelModule<CtSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, CtSeries series) {
        series.setConvolutionKernel((series.seriesType as CtSeriesType).sampleConvolutionKernel(equipment))
    }

}
