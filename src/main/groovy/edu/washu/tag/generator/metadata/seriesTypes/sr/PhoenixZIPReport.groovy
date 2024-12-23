package edu.washu.tag.generator.metadata.seriesTypes.sr

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR

class PhoenixZIPReport extends SrSeriesType {

    @Override
    String getSopClassUid() {
        UID.EnhancedSRStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        'PhoenixZIPReport'
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographmMR]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().addValue('OTHER').addValue('CSA REPORT')
    }

}
