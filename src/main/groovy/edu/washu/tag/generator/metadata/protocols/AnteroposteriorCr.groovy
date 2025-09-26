package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
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
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        switch (bodyPart) {
            case BodyPart.CHEST -> ProcedureCode.lookup('chest xray 1 view')
            case BodyPart.ABDOMEN -> ProcedureCode.lookup('abdomen xray 1 view')
            default -> throw new UnsupportedOperationException("Unsupported body part: ${bodyPart}")
        }
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.XR
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        randomizeWithBodyPart(randomizer, study.bodyPartExamined)
    }

}
