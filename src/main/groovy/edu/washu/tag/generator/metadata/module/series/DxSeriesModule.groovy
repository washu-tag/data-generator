package edu.washu.tag.generator.metadata.module.series

import org.dcm4che3.util.UIDUtils
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.PresentationIntentType
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.DxSeries
import edu.washu.tag.generator.util.RandomGenUtils

class DxSeriesModule implements SeriesLevelModule<DxSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, DxSeries series) {
        if (RandomGenUtils.weightedCoinFlip(10)) {
            series.setReferencedPerformedProcedureStepSopInstanceUid(UIDUtils.createUID())
        }
        series.setPresentationIntentType(PresentationIntentType.FOR_PRESENTATION)
    }

}
