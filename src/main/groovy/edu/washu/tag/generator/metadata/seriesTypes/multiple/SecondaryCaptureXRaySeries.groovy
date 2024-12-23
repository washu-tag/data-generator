package edu.washu.tag.generator.metadata.seriesTypes.multiple

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.util.RandomGenUtils

class SecondaryCaptureXRaySeries extends SecondaryCaptureSeries {

    private static final EnumeratedDistribution<String> xnatCompliantModalityRandomizer = RandomGenUtils.setupWeightedLottery([
            'DX' : 60,
            'CR' : 30,
            'OT' : 10
    ])
    private static final EnumeratedDistribution<String> generalModalityRandomizer = RandomGenUtils.setupWeightedLottery([
            'DX' : 40,
            'CR' : 25,
            'OT' : 15,
            'SC' : 15,
            'XR' : 5
    ])
    private static final EnumeratedDistribution<String> seriesDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Digitized Film' : 40,
            'Outside Image' : 30,
            'Scanned XRay' : 30
    ])

    @Override
    String getModality() {
        (specificationParameters.requireXnatCompatibility ? xnatCompliantModalityRandomizer : generalModalityRandomizer).sample()
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        seriesDescriptionRandomizer.sample()
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

}
