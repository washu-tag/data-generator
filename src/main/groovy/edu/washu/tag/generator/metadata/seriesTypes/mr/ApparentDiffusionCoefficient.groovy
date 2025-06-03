package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class ApparentDiffusionCoefficient extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'ADC' : 7,
            'Apparent Diffusion Coefficient (mm?/s)' : 3
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(15, 36)
    }

}
