package edu.washu.tag.generator.metadata.seriesTypes.pt

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue1
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue2
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class MracPetNonAttenuationCorrection extends PtSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            ("${BODYPART}_MRAC_PET_NAC Images".toString()) : 50,
            ("${BODYPART}_MRAC_PET_NAC".toString()) : 30,
            'MRAC_PET_NAC' : 30
    ])

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        replaceBodyPart(randomizer.sample(), bodyPartExamined)
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().addValue('STATIC').addValue('NAC')
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographmMR]
    }

    @Override
    boolean isAttenuationCorrected() {
        false
    }

    @Override
    PetSeriesTypeValue1 getSeriesTypeValue1() {
        PetSeriesTypeValue1.WHOLE_BODY
    }

    @Override
    PetSeriesTypeValue2 getSeriesTypeValue2() {
        PetSeriesTypeValue2.IMAGE
    }

    @Override
    List<PetImageCorrection> getImageCorrections(Series series) {
        [
                PetImageCorrection.NORM,
                PetImageCorrection.DTIM,
                PetImageCorrection.DECY,
                PetImageCorrection.FLEN,
                PetImageCorrection.RANSM,
                PetImageCorrection.XYSM,
                PetImageCorrection.ZSM
        ]
    }

}
