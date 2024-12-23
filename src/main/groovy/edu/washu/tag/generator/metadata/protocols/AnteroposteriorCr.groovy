package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.cr.AnteroposteriorCrSeries
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class AnteroposteriorCr extends Protocol {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            ("XR ${BODYPART} 1V PORTABLE".toString()) : 70,
            ("XR ${BODYPART} 1 VIEW AP OR PA".toString()) : 30
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new AnteroposteriorCrSeries()]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.ABDOMEN, BodyPart.CHEST]
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} X-ray"
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                "ZIV${bodyPart.offsetProcedureCode(37095)}",
                'UNKDEV',
                "XR ${bodyPart.dicomRepresentation} 1 VIEW",
                "${bodyPart.codeMeaning} 1 View Xray"
        )
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        randomizeWithBodyPart(randomizer, bodyPart)
    }

}
