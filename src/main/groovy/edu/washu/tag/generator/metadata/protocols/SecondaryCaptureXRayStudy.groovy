package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.multiple.SecondaryCaptureXRaySeries
import edu.washu.tag.generator.util.RandomGenUtils

class SecondaryCaptureXRayStudy extends SecondaryCaptureStudy {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'XR 1 VIEW' : 30,
            'OUTSIDE EXAM' : 20,
            'SCANNED XRAY' : 10
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new SecondaryCaptureXRaySeries()]
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('outside transfer xray')
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'single-view X-ray'
    }
    
}
