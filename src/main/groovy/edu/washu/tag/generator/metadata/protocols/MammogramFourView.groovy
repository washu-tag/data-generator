package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Laterality
import edu.washu.tag.generator.metadata.enums.contextGroups.MammographyView
import edu.washu.tag.generator.metadata.seriesTypes.mg.DigitalMammographyXRayForPresentation
import edu.washu.tag.generator.util.RandomGenUtils

class MammogramFourView extends Mammogram {

    public static final String SIMPLE_DESC = 'four-view mammogram'
    private static final String DIAG_ID = 'mammogram bilat diag'
    private static final String SCREEN_ID = 'mammogram bilat screen'
    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Screening Mamm Bilat' : 46,
            'SCREEN MAM-DIGITAL BILATERAL' : 30,
            'SCREENING MAMM BI' : 29,
            'Screening Mamm Bi' : 26,
            'DIGITAL SCREENING MAMM BILATERAL' : 11,
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
    String getStudyDescription(Equipment scanner, Study study) {
        if (study.procedureCodeId == SCREEN_ID) {
            studyDescriptionRandomizer.sample()
        } else {
            'Diag Mammogram Bilateral'
        }
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        SIMPLE_DESC
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup(RandomUtils.insecure().randomInt(0, 100) < 93 ? SCREEN_ID : DIAG_ID)
    }

}
