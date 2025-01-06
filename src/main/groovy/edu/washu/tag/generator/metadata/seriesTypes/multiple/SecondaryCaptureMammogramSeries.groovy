package edu.washu.tag.generator.metadata.seriesTypes.multiple

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.pixels.CachedPixelSpec
import edu.washu.tag.generator.metadata.pixels.PixelSpecification
import edu.washu.tag.generator.metadata.protocols.MammogramFourView
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.util.RandomGenUtils

class SecondaryCaptureMammogramSeries extends SecondaryCaptureSeries {

    private static final List<SeriesType> pixelSourceTypes = new MammogramFourView().allSeriesTypes

    private static final EnumeratedDistribution<String> xnatCompliantModalityRandomizer = RandomGenUtils.setupWeightedLottery([
            'MG' : 90,
            'OT' : 10
    ])
    private static final EnumeratedDistribution<String> generalModalityRandomizer = RandomGenUtils.setupWeightedLottery([
            'MG' : 80,
            'OT' : 10,
            'SC' : 10
    ])
    private static final EnumeratedDistribution<String> seriesDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Digitized Film' : 30,
            'Outside Image' : 30,
            'Scanned Mammogram' : 30,
            'MAMM 1 VIEW' : 10
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
    PixelSpecification pixelSpecFor(Series series, Instance instance) {
        RandomGenUtils.randomListEntry(pixelSourceTypes).pixelSpecFor(series, instance)
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

}
