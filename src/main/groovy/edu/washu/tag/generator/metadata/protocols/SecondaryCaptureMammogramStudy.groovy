package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.metadata.seriesTypes.multiple.SecondaryCaptureMammogramSeries
import edu.washu.tag.generator.util.RandomGenUtils

class SecondaryCaptureMammogramStudy extends SecondaryCaptureStudy {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'MAMMOGRAM 1 VIEW' : 30,
            'OUTSIDE EXAM' : 20,
            'Screen unilateral' : 10
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new SecondaryCaptureMammogramSeries()]
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.BREAST]
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('outside mg')
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.MAMMO
    }

    @Override
    boolean isApplicableFor(Patient patient) {
        patient.sex == Sex.FEMALE
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'single-view mammogram'
    }

}
