package edu.washu.tag.generator.metadata.seriesTypes.cr

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.pixels.CachedPixelSpec
import edu.washu.tag.generator.metadata.pixels.ZippedCachedPixelSpec
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.XRayViewPosition
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.series.CrSeriesModule
import edu.washu.tag.generator.metadata.scanners.PhilipsMobileDiagnostwDR
import edu.washu.tag.generator.util.RandomGenUtils

class AnteroposteriorCrSeries extends CrSeriesType {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'AP Grid' : 50,
            'AP Portrait' : 30,
            'AP Landscape' : 20
    ])

    @Override
    String getSopClassUid() {
        UID.ComputedRadiographyImageStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        randomizer.sample()
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [PhilipsMobileDiagnostwDR]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType()
    }

    @Override
    List<SeriesLevelModule> additionalSeriesModules() {
        [new CrSeriesModule()]
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    XRayViewPosition getViewPosition() {
        XRayViewPosition.AP
    }

    @Override
    CachedPixelSpec pixelSpecFor(Series series, Instance instance) {
        ZippedCachedPixelSpec.ofRsnaTestData('GEMS/CR/IM307')
    }

}
