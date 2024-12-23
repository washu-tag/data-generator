package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.mr.DiffusionTensorImage
import edu.washu.tag.generator.metadata.seriesTypes.mr.FluidAttenuatedInversionRecovery
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.SusceptibilityWeightedImage
import edu.washu.tag.generator.metadata.seriesTypes.mr.T1Weighted
import edu.washu.tag.generator.metadata.seriesTypes.mr.ThreePlaneLocalizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.UltrashortEchoTimeMrac
import edu.washu.tag.generator.metadata.seriesTypes.pt.MracPetNonAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.pt.MracPetWithAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.sr.PhoenixZIPReport
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class PetMr extends PetStudy {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            ("MR_PET_${BODYPART}".toString()) : 50,
            ("Clinical^${BODYPART}".toString()) : 30,
            ("MR-PET ${BODYPART}".toString()) : 15,
            'PET-MR Study' : 5
    ])

    private static final List<SeriesType> seriesTypes = [
            new Localizer(),
            new FluidAttenuatedInversionRecovery(),
            new T1Weighted(anatomicalPlane : AnatomicalPlane.TRANSVERSE),
            new DiffusionTensorImage(anatomicalPlane : AnatomicalPlane.TRANSVERSE),
            new UltrashortEchoTimeMrac(),
            new MracPetNonAttenuationCorrection(),
            new MracPetWithAttenuationCorrection(),
            new SusceptibilityWeightedImage(),
            new PhoenixZIPReport()
    ]

    @Override
    List<SeriesType> getAllSeriesTypes() {
        seriesTypes
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [
                BodyPart.ABDOMEN,
                BodyPart.CHEST,
                BodyPart.HEAD,
                BodyPart.PELVIS
        ]
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        randomizeWithBodyPart(studyDescriptionRandomizer, bodyPart)
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                "ZIV${bodyPart.offsetProcedureCode(50143)}",
                'UNKDEV',
                "PET-MR ${bodyPart.dicomRepresentation}",
                "${bodyPart.codeMeaning} PtMr"
        )
    }

    @Override
    void resample(Patient patient) {
        super.resample(patient)
        seriesTypes.remove(0)
        seriesTypes.add(0, ThreadLocalRandom.current().nextBoolean() ? new Localizer(anatomicalPlane: AnatomicalPlane.TRANSVERSE) : new ThreePlaneLocalizer())
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} PET-MR"
    }
}
