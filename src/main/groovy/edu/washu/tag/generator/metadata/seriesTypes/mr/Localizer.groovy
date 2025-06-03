package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class Localizer extends MrSeriesType {

    private static final EnumeratedDistribution<String> seriesDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'localizer_%PLANE%' : 30,
            '%PLANE% LOC' : 20,
            '%PLANE% Scout' : 20,
            'scout_%PLANE%' : 15
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        seriesDescriptionRandomizer
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        super.compatibleEquipment + [SiemensBiographmMR] as List<Class<? extends Equipment>>
    }

    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(1, 21)
    }

}
