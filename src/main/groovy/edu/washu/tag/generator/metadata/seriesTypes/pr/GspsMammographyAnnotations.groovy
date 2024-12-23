package edu.washu.tag.generator.metadata.seriesTypes.pr

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.scanners.HologicSecurViewDx
import edu.washu.tag.generator.util.RandomGenUtils

class GspsMammographyAnnotations extends PrSeriesType {

    private static final EnumeratedDistribution<String> seriesDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Presentation State' : 40,
            'MASS ANNOTATIONS' : 30,
            'Annotation - Breast' : 30,
            'Measurements' : 10
    ])

    @Override
    String getSopClassUid() {
        UID.GrayscaleSoftcopyPresentationStateStorage
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        seriesDescriptionRandomizer.sample()
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [HologicSecurViewDx]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        null
    }

    @Override
    SeriesBundling seriesBundling() {
        SeriesBundling.PR_MG
    }

}
