package edu.washu.tag.generator.metadata.module.series

import org.dcm4che3.util.UIDUtils
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.SrSeries
import edu.washu.tag.generator.util.RandomGenUtils

class SrDocumentSeriesModule implements SeriesLevelModule<SrSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, SrSeries series) {
        if (RandomGenUtils.weightedCoinFlip(20)) {
            series.setReferencedPerformedProcedureStepSopInstanceUid(UIDUtils.createUID())
        }
    }

}
