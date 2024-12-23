package edu.washu.tag.generator.metadata.seriesTypes.ko

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.series.KeyObjectDocumentSeriesModule
import edu.washu.tag.generator.metadata.scanners.SwiftPacs

class InstanceEnumerationKeyObjectNote extends KoSeriesType {

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        'Instance Enumeration'
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SwiftPacs]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        null
    }

    @Override
    List<SeriesLevelModule> allSeriesModules() {
        [new KeyObjectDocumentSeriesModule()]
    }

    @Override
    SeriesBundling seriesBundling() {
        SeriesBundling.KO
    }

}
