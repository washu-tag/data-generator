package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.util.RandomGenUtils

class DynamicSusceptibilityContrast extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'DSC PERFUSION %PLANE%' : 50,
            'DSC PERF' : 30,
            'DSC' : 20,
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    int producedInstanceCount() {
        10 // TODO: how realistic is this?
    }

}
