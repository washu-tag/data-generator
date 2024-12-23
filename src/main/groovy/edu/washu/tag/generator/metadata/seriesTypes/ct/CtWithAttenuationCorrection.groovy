package edu.washu.tag.generator.metadata.seriesTypes.ct

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.scanners.CtScanner
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.util.RandomGenUtils

class CtWithAttenuationCorrection extends CtSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'CT_AC' : 70,
            'CTAC' : 30
    ])

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        randomizer.sample()
    }

    @Override
    ImageType resolveImageType(CtScanner equipment) {
        equipment.getAttenuationCorrectedCtImageType()
    }

}
