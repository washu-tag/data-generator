package edu.washu.tag.generator.metadata.seriesTypes.xa

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.pixels.CachedPixelSpec
import edu.washu.tag.generator.metadata.pixels.ZippedCachedPixelSpec
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.scanners.XaScanner

abstract class XaSeriesType extends SeriesType {

    @Override
    String getModality() {
        'XA'
    }

    @Override
    String getSopClassUid() {
        UID.XRayAngiographicImageStorage
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        getXaImageType(equipment as XaScanner).resolve()
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    CachedPixelSpec pixelSpecFor(Series series, Instance instance) {
        ZippedCachedPixelSpec.ofRsnaTestData('TOSHIBA/PAT00058/STD00059/SFS00060/OBJ00061') // TODO: this could probably be better
    }

    abstract XaImageType getXaImageType(XaScanner scanner)

}
