package edu.washu.tag.generator.metadata.seriesTypes.ko

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.series.KoSeries

abstract class KoSeriesType extends SeriesType {

    @Override
    String getModality() {
        'KO'
    }

    @Override
    String getSopClassUid() {
        UID.KeyObjectSelectionDocumentStorage
    }

    @Override
    Class<? extends Series> seriesClass() {
        KoSeries
    }

}
