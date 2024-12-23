package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class UltrashortEchoTimeMrac extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'UTE_MRAC' : 50,
            ("${BODYPART}_UTE_MRAC".toString()) : 30,
            ("UTE_MRAC_${BODYPART}".toString()) : 20
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().
                addValue('M').
                addValue('NORM').
                addValue('DIS3D').
                addValue('DIS2D')
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographmMR]
    }

}
