package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

class DiffusionTensorImage extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'DTI DIFF' : 30,
            '%PLANE% DTI' : 30,
            'DTI' : 20
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        super.compatibleEquipment + [SiemensBiographmMR] as List<Class<? extends Equipment>>
    }

    @Override
    int producedInstanceCount() {
        25
    }

}
