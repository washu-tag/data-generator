package edu.washu.tag.generator.metadata.seriesTypes.mr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class ThreePlaneLocalizer extends MrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            '3 Plane Localizer' : 100,
            '3 PLANE LOC' : 40,
            '3 Plane Localizer_ND' : 35,
            '3-plane localizer' : 30,
            '3Plane Loc' : 15,
            'localizer 3 plane' : 5
    ])

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        randomizer
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().addValue('M').addValue('ND')
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        super.compatibleEquipment + [SiemensBiographmMR] as List<Class<? extends Equipment>>
    }

    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(25, 51)
    }

}
