package edu.washu.tag.generator.metadata.seriesTypes.sr

import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.series.SrDocumentSeriesModule
import edu.washu.tag.generator.metadata.series.SrSeries

abstract class SrSeriesType extends SeriesType {

    @Override
    String getModality() {
        'SR'
    }

    @Override
    List<SeriesLevelModule> additionalSeriesModules() {
        [new SrDocumentSeriesModule()]
    }

    @Override
    Class<? extends Series> seriesClass() {
        SrSeries
    }

}
