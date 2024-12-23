package edu.washu.tag.generator.metadata.seriesTypes.cr

import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.XRayViewPosition
import edu.washu.tag.generator.metadata.series.CrSeries

abstract class CrSeriesType extends SeriesType {

    @Override
    String getModality() {
        'CR'
    }

    @Override
    Class<? extends Series> seriesClass() {
        CrSeries
    }

    abstract XRayViewPosition getViewPosition()

}
