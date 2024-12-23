package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.CrSeries
import edu.washu.tag.generator.metadata.seriesTypes.cr.CrSeriesType

class CrSeriesModule implements SeriesLevelModule<CrSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, CrSeries series) {
        if (series.bodyPartExamined == null) {
            series.setBodyPartExamined(BodyPart.UNKNOWN) // element overwritten as type 2 by this module
        }
        series.setViewPosition((series.seriesType as CrSeriesType).viewPosition)
        series.setFilterType('0.1Cu,1Al')
        series.setFocalSpots(['2.0'])
        series.setPlateType('Sel Drum 500x500')
    }

}
