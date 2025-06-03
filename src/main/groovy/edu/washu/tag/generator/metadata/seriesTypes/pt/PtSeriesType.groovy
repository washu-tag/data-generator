package edu.washu.tag.generator.metadata.seriesTypes.pt

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue1
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue2
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.series.NmPetOrientationModule
import edu.washu.tag.generator.metadata.module.series.PetIsotopeModule
import edu.washu.tag.generator.metadata.module.series.PetSeriesModule
import edu.washu.tag.generator.metadata.scanners.PhilipsGeminiR35
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64
import edu.washu.tag.generator.metadata.series.PtSeries

import java.util.concurrent.ThreadLocalRandom

abstract class PtSeriesType extends SeriesType {

    @Override
    String getModality() {
        'PT'
    }

    @Override
    String getSopClassUid() {
        UID.PositronEmissionTomographyImageStorage
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographTruePoint64, PhilipsGeminiR35]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType()
    }

    @Override
    List<SeriesLevelModule> additionalSeriesModules() {
        [
                new PetSeriesModule(),
                new PetIsotopeModule(),
                new NmPetOrientationModule()
        ]
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    Class<? extends Series> seriesClass() {
        PtSeries
    }

    /*
    TODO: multiple series in a study usually have the same instance count in practice. How
    do we correlate between series here?
     */
    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(200, 401)
    }

    abstract boolean isAttenuationCorrected()
    abstract PetSeriesTypeValue1 getSeriesTypeValue1()
    abstract PetSeriesTypeValue2 getSeriesTypeValue2()
    abstract List<PetImageCorrection> getImageCorrections(Series series)

}
