package edu.washu.tag.generator.metadata.seriesTypes.pt

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue1
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue2
import edu.washu.tag.generator.metadata.scanners.PetScanner
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class PtNonAttenuationCorrected extends PtSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            ("PET ${BODYPART} (NAC)".toString()) : 50,
            'PT_NAC' : 30,
            ("PET ${BODYPART} Uncorrected".toString()) : 20
    ])

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        replaceBodyPart(randomizer.sample(), bodyPartExamined)
    }

    @Override
    boolean isAttenuationCorrected() {
        false
    }

    @Override
    PetSeriesTypeValue1 getSeriesTypeValue1() {
        PetSeriesTypeValue1.DYNAMIC
    }

    @Override
    PetSeriesTypeValue2 getSeriesTypeValue2() {
        PetSeriesTypeValue2.IMAGE
    }

    @Override
    List<PetImageCorrection> getImageCorrections(Series series) {
        (series.scanner as PetScanner).nonAttenuatedCorrections
    }

}
