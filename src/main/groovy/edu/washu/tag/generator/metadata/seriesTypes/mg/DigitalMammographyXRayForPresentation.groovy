package edu.washu.tag.generator.metadata.seriesTypes.mg

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.pixels.NestedZipPixelSource
import edu.washu.tag.generator.metadata.pixels.PixelSource
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Laterality
import edu.washu.tag.generator.metadata.enums.contextGroups.MammographyView
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.series.DxSeriesModule
import edu.washu.tag.generator.metadata.module.series.MammographySeriesModule
import edu.washu.tag.generator.metadata.module.series.MgSeries
import edu.washu.tag.generator.metadata.scanners.LoradSelenia

class DigitalMammographyXRayForPresentation extends SeriesType {

    final Laterality laterality
    final MammographyView viewPosition
    private final String description

    DigitalMammographyXRayForPresentation(Laterality laterality, MammographyView viewPosition) {
        this.laterality = laterality
        this.viewPosition = viewPosition
        description = "${laterality.dicomRepresentation} ${viewPosition.dicomRepresentation}"
    }

    @Override
    String getModality() {
        'MG'
    }

    @Override
    String getSopClassUid() {
        UID.DigitalMammographyXRayImageStorageForPresentation
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        description
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [LoradSelenia]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType().derived().secondary().addValue('') // PS3.3 C.8.11.7.1.4
    }

    @Override
    Laterality laterality() {
        laterality
    }

    @Override
    List<SeriesLevelModule> additionalSeriesModules() {
        [new DxSeriesModule(), new MammographySeriesModule()]
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    PixelSource pixelSourceFor(Series series, Instance instance) {
        final String lateralityAndView = laterality.dicomRepresentation + viewPosition.dicomRepresentation
        new NestedZipPixelSource(
            'https://download.nrg.wustl.edu/pub/data/xnat_populate/Mammo_MEG.zip',
            'Mammo_MEG.zip',
            'Mammo_MEG/001001_CUR.zip',
            "001001_CUR/CUR${lateralityAndView}",
            lateralityAndView
        )
    }

    @Override
    Class<? extends Series> seriesClass() {
        MgSeries
    }

}
