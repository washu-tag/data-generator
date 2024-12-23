package edu.washu.tag.generator.metadata.seriesTypes.nm

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.NmScanType
import edu.washu.tag.generator.util.RandomGenUtils

class RestingNm extends NmSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'REST' : 70,
            'Resting' : 30
    ])

    @Override
    NmImageType getImageType() {
        new NmImageType().scanType(NmScanType.TOMO)
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        randomizer.sample()
    }

}
