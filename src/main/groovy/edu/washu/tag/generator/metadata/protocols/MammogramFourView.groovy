package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Laterality
import edu.washu.tag.generator.metadata.enums.contextGroups.MammographyView
import edu.washu.tag.generator.metadata.seriesTypes.mg.DigitalMammographyXRayForPresentation
import edu.washu.tag.generator.util.RandomGenUtils

class MammogramFourView extends Mammogram {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Screening Mamm Bilat' : 46,
            'SCREEN MAM-DIGITAL BILATERAL' : 30,
            'SCREENING MAMM BI' : 29,
            'Screening Mamm Bi' : 26,
            'DIGITAL SCREENING MAMM BILATERAL' : 11,
            'Diag Mammogram Bilateral' : 10,
            'DIGITAL MAMMOGRAM,BILATERAL' : 10
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new DigitalMammographyXRayForPresentation(Laterality.RIGHT, MammographyView.CC),
                new DigitalMammographyXRayForPresentation(Laterality.LEFT, MammographyView.CC),
                new DigitalMammographyXRayForPresentation(Laterality.RIGHT, MammographyView.MLO),
                new DigitalMammographyXRayForPresentation(Laterality.LEFT, MammographyView.MLO)
        ]
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'four-view mammogram'
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV70047',
                'UNKDEV',
                'SCREENING MAMM BILAT',
                'Screening Mammogram Bilateral'
        )
    }

}
