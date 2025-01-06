package edu.washu.tag.generator.metadata.seriesTypes.us

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.pixels.CachedPixelSpec
import edu.washu.tag.generator.metadata.pixels.ZippedCachedPixelSpec
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.scanners.AcusonSequoia
import edu.washu.tag.generator.metadata.scanners.PhilipsiE33

class UsSeriesType extends SeriesType {

    @Override
    String getModality() {
        'US'
    }

    @Override
    String getSopClassUid() {
        UID.UltrasoundImageStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        null // not a mistake, this element is often left out of ultrasounds
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [AcusonSequoia, PhilipsiE33]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().addValue('').addValue('0001')
    }

    @Override
    CachedPixelSpec pixelSpecFor(Series series, Instance instance) {
        ZippedCachedPixelSpec.ofRsnaTestData('GEMS/US/IM13')
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

}
