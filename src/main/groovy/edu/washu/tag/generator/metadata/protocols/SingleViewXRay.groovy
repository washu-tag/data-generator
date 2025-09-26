package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.*
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.dx.DigitalXRayForPresentation
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

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
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup("xray ${bodyPart.codeMeaning.toLowerCase()}")
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.XR
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} X-ray"
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        randomizeWithBodyPart(randomizer, study.bodyPartExamined)
    }

}
