package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.mr.*
import edu.washu.tag.generator.util.RandomGenUtils

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
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV00306',
                'UNKDEV',
                'BRAIN DMRI',
                'Brain dMRI'
        )
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'brain MRI'
    }

}
