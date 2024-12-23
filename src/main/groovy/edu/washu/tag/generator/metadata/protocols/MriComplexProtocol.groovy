package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.seriesTypes.mr.BloodOxygenLevelDependentImage
import edu.washu.tag.generator.metadata.seriesTypes.mr.DiffusionTensorImage
import edu.washu.tag.generator.metadata.seriesTypes.mr.DiffusionWeightedImage
import edu.washu.tag.generator.metadata.seriesTypes.mr.FluidAttenuatedInversionRecovery
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.T1Weighted
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Star
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Weighted
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class MriComplexProtocol extends Protocol {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            ("MRI ${BODYPART} WITH AND WITHOUT CONTRAST".toString()) : 50,
            ("MR ${BODYPART} WO+W CONTRAST".toString()) : 30,
            ("MRI ${BODYPART}".toString()) : 10,
            ("${BODYPART} MR IMAG".toString()) : 5
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Localizer(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new T1Weighted(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new T2Weighted(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new DiffusionWeightedImage(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new DiffusionTensorImage(anatomicalPlane: AnatomicalPlane.SAGITTAL),
                new FluidAttenuatedInversionRecovery(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new T2Star(anatomicalPlane: AnatomicalPlane.SAGITTAL),
                new T1Weighted(anatomicalPlane: AnatomicalPlane.TRANSVERSE).withContrast(),
                new BloodOxygenLevelDependentImage(anatomicalPlane: AnatomicalPlane.CORONAL)
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [
                BodyPart.ABDOMEN,
                BodyPart.BRAIN,
                BodyPart.CHEST,
                BodyPart.HEAD,
                BodyPart.HEART
        ]
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} MRI"
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV' + bodyPart.offsetProcedureCode(30190),
                'UNKDEV',
                "MRI ${bodyPart.dicomRepresentation} DIAGNOSTIC",
                "${bodyPart.codeMeaning} MRI"
        )
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        randomizeWithBodyPart(studyDescriptionRandomizer, bodyPart)
    }

}
