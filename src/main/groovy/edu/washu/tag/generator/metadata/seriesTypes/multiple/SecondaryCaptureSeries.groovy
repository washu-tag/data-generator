package edu.washu.tag.generator.metadata.seriesTypes.multiple

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.scanners.LumisysLS75

abstract class SecondaryCaptureSeries extends SeriesType {

    @Override
    String getSopClassUid() {
        UID.SecondaryCaptureImageStorage
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [LumisysLS75]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().derived().secondary().addValue('UNCOMPRESSED')
    }

}
