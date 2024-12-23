package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

class FluidAttenuatedInversionRecovery extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            '%PLANE% FLAIR T2' : 30,
            '%PLANE% FLAIR T1' : 20,
            'FLAIR' : 20,
            'T1 %PLANE% FLAIR' : 10
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        super.compatibleEquipment + [SiemensBiographmMR] as List<Class<? extends Equipment>>
    }

}
