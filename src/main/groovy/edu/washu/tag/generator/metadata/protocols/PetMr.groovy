package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.*
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.mr.*
import edu.washu.tag.generator.metadata.seriesTypes.pt.MracPetNonAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.pt.MracPetWithAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.sr.PhoenixZIPReport
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class PetMr extends PetStudy {

    private final Logger logger = LoggerFactory.getLogger(PetMr)

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            ("MR_PET_${BODYPART}".toString()) : 50,
            ("Clinical^${BODYPART}".toString()) : 30,
            ("MR-PET ${BODYPART}".toString()) : 15,
            'PET-MR Study' : 5
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
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
    String getStudyDescription(Equipment scanner, Study study) {
        randomizeWithBodyPart(studyDescriptionRandomizer, study.bodyPartExamined)
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup("petmr ${bodyPart.codeMeaning.toLowerCase()}")
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
