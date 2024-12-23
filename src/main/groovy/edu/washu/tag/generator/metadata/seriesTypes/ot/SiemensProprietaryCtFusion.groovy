package edu.washu.tag.generator.metadata.seriesTypes.ot

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64

class SiemensProprietaryCtFusion extends OtSeriesType {

    @Override
    String getSopClassUid() {
        UID.PrivateSiemensCSANonImageStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        '3D Application Data'
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographTruePoint64]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().derived().secondary().addValue('OTHER').addValue('CSA 3D FUSION')
    }

}
