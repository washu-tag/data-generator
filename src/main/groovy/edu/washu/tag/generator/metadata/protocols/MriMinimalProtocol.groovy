package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.T1Weighted
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Weighted
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class MriMinimalProtocol extends Protocol {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            ("MRI ${BODYPART} WITHOUT CONTRAST".toString()) : 50,
            ("MRI ${BODYPART} EXPEDITED PROTOCOL".toString()) : 25,
            ("MR ${BODYPART} WO CONTRAST".toString()) : 30,
            ("MRI ${BODYPART} W/O CONTRAST".toString()) : 10
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Localizer(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new T2Weighted(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new T1Weighted(anatomicalPlane: AnatomicalPlane.TRANSVERSE)
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [
                BodyPart.BRAIN,
                BodyPart.CHEST,
                BodyPart.HEAD,
                BodyPart.HEART
        ]
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV' + bodyPart.offsetProcedureCode(86091),
                'UNKDEV',
                "MRI ${bodyPart.dicomRepresentation} EXP",
                "${bodyPart.codeMeaning} MRI Expedited"
        )
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} MRI"
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        randomizeWithBodyPart(studyDescriptionRandomizer, bodyPart)
    }

}