package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

class T1Weighted extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizerWithContrast = RandomGenUtils.setupWeightedLottery([
            't1w_%PLANE%_post' : 30,
            '%PLANE% T1 +C' : 30,
            'T1post %PLANE%' : 20,
            'T1W_%PLANE%_W_CONTRAST' : 10
    ])
    private static final EnumeratedDistribution<String> randomizerWithoutContrast = RandomGenUtils.setupWeightedLottery([
            't1w_%PLANE%_pre' : 30,
            '%PLANE% T1' : 30,
            'T1pre %PLANE%' : 20,
            'T1W_%PLANE%_WO_CONTRAST' : 10
    ])

    boolean hasContrast = false

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        hasContrast ? randomizerWithContrast : randomizerWithoutContrast
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        super.compatibleEquipment + [SiemensBiographmMR] as List<Class<? extends Equipment>>
    }

    T1Weighted withContrast() {
        hasContrast = true
        this
    }

}
