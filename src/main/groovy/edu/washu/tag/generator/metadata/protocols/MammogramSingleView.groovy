package edu.washu.tag.generator.metadata.protocols


import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Laterality
import edu.washu.tag.generator.metadata.enums.contextGroups.MammographyView
import edu.washu.tag.generator.metadata.seriesTypes.mg.DigitalMammographyXRayForPresentation
import edu.washu.tag.generator.util.RandomGenUtils

class MammogramSingleView extends Mammogram {

    private static final String DIAG_ID = 'mammogram unilat diag'
    private static final String SCREEN_ID = 'mammogram unilat screen'
    private static final List<SeriesType> possibleSeriesTypes = cacheSeries()
    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Diag Mammogram Unilateral' : 15,
            'LIMITED MAMMOGRAM, UNILATERAL' : 5,
            'DIGITAL MAMMOGRAM,UNILATERAL' : 3
    ])

    @Override
    void resample(Patient patient) {
        setSeriesTypes(getAllSeriesTypes())
    }

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [RandomGenUtils.randomListEntry(possibleSeriesTypes)]
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        if (study.procedureCodeId == SCREEN_ID) {
            'Screen unilateral'
        } else {
            studyDescriptionRandomizer.sample()
        }
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'single-view mammogram'
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup(RandomUtils.insecure().randomInt(0, 100) < 96 ? DIAG_ID : SCREEN_ID)
    }

    private static List<SeriesType> cacheSeries() {
        [MammographyView.MLO, MammographyView.CC].collectMany { position ->
            [Laterality.LEFT, Laterality.RIGHT].collect { laterality ->
                new DigitalMammographyXRayForPresentation(laterality, position)
            }
        }
    }

}
