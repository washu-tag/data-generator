package edu.washu.tag.generator.metadata.seriesTypes.nm

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.pixels.CachedPixelSpec
import edu.washu.tag.generator.metadata.pixels.ZippedCachedPixelSpec
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.series.NmPetOrientationModule
import edu.washu.tag.generator.metadata.scanners.GEInfinia
import edu.washu.tag.generator.metadata.series.NmSeries

abstract class NmSeriesType extends SeriesType {

    @Override
    String getModality() {
        'NM'
    }

    @Override
    String getSopClassUid() {
        UID.NuclearMedicineImageStorage
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [GEInfinia]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        getImageType().resolve()
    }

    abstract NmImageType getImageType()

    @Override
    List<SeriesLevelModule> additionalSeriesModules() {
        [new NmPetOrientationModule()]
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    CachedPixelSpec pixelSpecFor(Series series, Instance instance) {
        ZippedCachedPixelSpec.ofRsnaTestData('GEMS/NM/IM261.dcma')
    }

    @Override
    Class<? extends Series> seriesClass() {
        NmSeries
    }

}
