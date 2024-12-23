package edu.washu.tag.generator.metadata.seriesTypes.radiotherapy

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Randomizeable
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.series.RtSeriesModule
import edu.washu.tag.generator.metadata.scanners.AdacPinnacle3
import edu.washu.tag.generator.metadata.series.RtstructSeries
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.StringReplacements

class RtstructSeriesType extends SeriesType implements Randomizeable {

    private final String referencedImageType
    private static final String TYPE_KEY = '%TYPE%'
    private static final EnumeratedDistribution<String> seriesDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            ("RTstruct_${TYPE_KEY}".toString()) : 40,
            'RT Structures' : 30,
            ("${StringReplacements.BODYPART}".toString()) : 20,
            'RT Data' : 10
    ])

    RtstructSeriesType(String type) {
        referencedImageType = type
    }

    @Override
    String getModality() {
        'RTSTRUCT'
    }

    @Override
    String getSopClassUid() {
        UID.RTStructureSetStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        randomizeWithBodyPart(seriesDescriptionRandomizer, bodyPartExamined).replace(TYPE_KEY, referencedImageType)
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [AdacPinnacle3]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        null
    }

    @Override
    List<SeriesLevelModule> allSeriesModules() {
        [new RtSeriesModule()]
    }

    @Override
    Class<? extends Series> seriesClass() {
        RtstructSeries
    }

    @Override
    SeriesBundling seriesBundling() {
        SeriesBundling.RTSTRUCT
    }

}
