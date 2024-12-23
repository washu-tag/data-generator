package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
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

    private static final List<SeriesType> possibleSeriesTypes = cacheSeries()
    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'Diag Mammogram Unilateral' : 15,
            'LIMITED MAMMOGRAM, UNILATERAL' : 5,
            'Screen unilateral' : 3,
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
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'single-view mammogram'
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV70046',
                'UNKDEV',
                'DIAG MAMM UNILATERAL',
                'Diagnostic Mammogram Unilateral'
        )
    }

    private static List<SeriesType> cacheSeries() {
        [MammographyView.MLO, MammographyView.CC].collectMany { position ->
            [Laterality.LEFT, Laterality.RIGHT].collect { laterality ->
                new DigitalMammographyXRayForPresentation(laterality, position)
            }
        }
    }

}
