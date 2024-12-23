package edu.washu.tag.generator.metadata.seriesTypes.ct

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.scanners.CtScanner
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.util.RandomGenUtils

class Topogram extends CtSeriesType {

    private static final EnumeratedDistribution<Double> sliceThicknesses = RandomGenUtils.setupWeightedLottery([
            (0.6) : 30,
            (1.0) : 50,
            (3.0) : 50,
            (5.0) : 80
    ]) as EnumeratedDistribution<Double>

    private static final EnumeratedDistribution<String> kernels = RandomGenUtils.setupWeightedLottery([
            'T80f' : 50,
            'T80s' : 50
    ])

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        "Topogram ${sliceThicknesses.sample()} ${kernels.sample()}"
    }

    @Override
    ImageType resolveImageType(CtScanner equipment) {
        equipment.getTopogramImageType()
    }

}
