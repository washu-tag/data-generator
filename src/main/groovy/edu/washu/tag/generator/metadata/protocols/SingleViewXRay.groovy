package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.dx.DigitalXRayForPresentation
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class SingleViewXRay extends Protocol {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            ("XR ${BODYPART} 1V".toString()) : 100,
            (BODYPART) : 50
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new DigitalXRayForPresentation()]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [
                BodyPart.ABDOMEN,
                BodyPart.CERVICAL_SPINE,
                BodyPart.CHEST,
                BodyPart.SKULL,
                BodyPart.SPINE,
                BodyPart.THORACIC_SPINE
        ]
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                "ZIV0${bodyPart.offsetProcedureCode(4343)}",
                'UNKDEV',
                "XR ${bodyPart.dicomRepresentation} 1V DX",
                "${bodyPart.codeMeaning} 1 View Dx Xray"
        )
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} X-ray"
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        randomizeWithBodyPart(randomizer, bodyPart)
    }

}
