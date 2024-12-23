package edu.washu.tag.generator.metadata.seriesTypes.pr

import edu.washu.tag.generator.metadata.SeriesType

abstract class PrSeriesType extends SeriesType {

    @Override
    String getModality() {
        'PR'
    }

}
