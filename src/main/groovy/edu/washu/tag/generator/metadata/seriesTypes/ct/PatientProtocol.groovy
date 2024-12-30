package edu.washu.tag.generator.metadata.seriesTypes.ct

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.scanners.CtScanner
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64

class PatientProtocol extends CtSeriesType {

    @Override
    String getSopClassUid() {
        UID.SecondaryCaptureImageStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        'Patient Protocol'
    }

    @Override
    ImageType resolveImageType(CtScanner equipment) {
        new ImageType().derived().secondary().addValue('OTHER').addValue('CT_SOM5 PROT')
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographTruePoint64]
    }

}