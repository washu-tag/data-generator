package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.RtSeries

class RtSeriesModule implements SeriesLevelModule<RtSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, RtSeries series) {
        GeneralSeriesModule.setModality(series)
        GeneralSeriesModule.setSeriesInstanceUid(series)
        GeneralSeriesModule.setSeriesNumber(series)
        GeneralSeriesModule.setSeriesDate(study, series)
        GeneralSeriesModule.setSeriesTime(study, series)
        GeneralSeriesModule.setSeriesDescription(study, series)
        series.setOperatorsName(study.operatorMap?.get(equipment) ?: ['']) // type 3 in General Series Module, but type 2 in this module
        // TODO: MPPS?
        // TODO: Request AttributesSequence
        // TODO: 10.13 Performed Procedure Step Summary Macro
    }

}
