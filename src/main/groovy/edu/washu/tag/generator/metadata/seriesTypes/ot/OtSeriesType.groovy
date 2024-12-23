package edu.washu.tag.generator.metadata.seriesTypes.ot

import edu.washu.tag.generator.metadata.SeriesType

abstract class OtSeriesType extends SeriesType {

    @Override
    String getModality() {
        'OT'
    }
    
}
