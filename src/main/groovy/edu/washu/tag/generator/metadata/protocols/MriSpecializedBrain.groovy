package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.*
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.mr.*
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

class MriSpecializedBrain extends Protocol {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'MRI BRAIN WITH AND WITHOUT CONTRAST' : 50,
            'MR BRAIN WO+W CONTRAST' : 30,
            'MRI Diff/Perf Weighted' : 30,
            'mri_w_wo_contrast' : 20
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Localizer(anatomicalPlane: AnatomicalPlane.SAGITTAL),
                new T1Weighted(anatomicalPlane: AnatomicalPlane.SAGITTAL),
                new SusceptibilityWeightedImage(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new DynamicContrastEnhanced(anatomicalPlane: AnatomicalPlane.SAGITTAL),
                new ApparentDiffusionCoefficient(anatomicalPlane: AnatomicalPlane.SAGITTAL),
                new DynamicSusceptibilityContrast(anatomicalPlane: AnatomicalPlane.SAGITTAL)
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.BRAIN]
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('mri brain perf wwo')
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.MRI
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'brain MRI'
    }

}
