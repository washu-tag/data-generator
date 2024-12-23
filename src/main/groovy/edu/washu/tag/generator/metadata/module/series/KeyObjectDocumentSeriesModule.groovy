package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.KoSeries

class KeyObjectDocumentSeriesModule implements SeriesLevelModule<KoSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, KoSeries series) {
        GeneralSeriesModule.setModality(series)
        GeneralSeriesModule.setSeriesInstanceUid(series)
        GeneralSeriesModule.setSeriesNumber(series)
        GeneralSeriesModule.setSeriesDate(study, series)
        GeneralSeriesModule.setSeriesTime(study, series)
        GeneralSeriesModule.setSeriesDescription(study, series)
        GeneralSeriesModule.setProtocolName(study, series)
        // TODO: MPPS?
    }

}
