package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class DiffusionWeightedImage extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            '%PLANE% DWI' : 50,
            'DWI' : 30,
            'Diffusion Weighted' : 20
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    int producedInstanceCount() {
        2 * ThreadLocalRandom.current().nextInt(16, 65)
    }

}
