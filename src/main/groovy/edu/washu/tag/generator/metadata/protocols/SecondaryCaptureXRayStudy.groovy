package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
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
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'SC54513',
                'UNKDEV',
                'OUTSIDE XRAY',
                'Outside Xray'
        )
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'single-view X-ray'
    }
    
}
