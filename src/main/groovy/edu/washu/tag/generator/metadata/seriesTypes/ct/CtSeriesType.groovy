package edu.washu.tag.generator.metadata.seriesTypes.ct

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.scanners.CtScanner
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.scanners.PhilipsGeminiR35
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64

abstract class CtSeriesType extends SeriesType {

    @Override
    String getModality() {
        'CT'
    }

    @Override
    String getSopClassUid() {
        UID.CTImageStorage
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographTruePoint64, PhilipsGeminiR35]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        resolveImageType(equipment as CtScanner)
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    abstract ImageType resolveImageType(CtScanner equipment)

}
