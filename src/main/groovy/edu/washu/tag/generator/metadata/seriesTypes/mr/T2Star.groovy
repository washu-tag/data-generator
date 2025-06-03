package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class T2Star extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'T2star_%PLANE%' : 50,
            'T2* %PLANE%' : 30,
            '%PLANE% t2*' : 20
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(19, 40)
    }

}
