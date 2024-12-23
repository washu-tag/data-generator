package edu.washu.tag.generator.metadata.seriesTypes.dx

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.series.DxSeriesModule
import edu.washu.tag.generator.metadata.scanners.GEDiscoveryXR656
import edu.washu.tag.generator.metadata.series.DxSeries

import java.util.concurrent.ThreadLocalRandom

class DigitalXRayForPresentation extends SeriesType {

    @Override
    String getModality() {
        'DX'
    }

    @Override
    String getSopClassUid() {
        UID.DigitalXRayImageStorageForPresentation
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        ThreadLocalRandom.current().nextBoolean() ? bodyPartExamined.dicomRepresentation : bodyPartExamined.code.codeMeaning
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [GEDiscoveryXR656]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().addValue('')
    }

    @Override
    List<SeriesLevelModule> additionalSeriesModules() {
        [new DxSeriesModule()]
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    Class<? extends Series> seriesClass() {
        DxSeries
    }

}
