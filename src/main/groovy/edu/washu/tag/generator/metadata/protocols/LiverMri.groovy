package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.ContrastAgent
import edu.washu.tag.generator.metadata.enums.ContrastPhase
import edu.washu.tag.generator.metadata.seriesTypes.mr.DiffusionWeightedImage
import edu.washu.tag.generator.metadata.seriesTypes.mr.InOpposedPhaseT1
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.MultiphaseT1
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Weighted
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

/**
 * Multiphase contrast-enhanced liver MRI, as used for LI-RADS diagnostic imaging. With an extracellular agent (the default)
 * or gadobenate, the dynamic phases are precontrast, late arterial, portal venous, and delayed. With gadoxetate,
 * the delayed phase is replaced by the transitional and hepatobiliary phases.
 */
class LiverMri extends Protocol {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'MRI ABDOMEN W WO CONTRAST LIVER PROTOCOL' : 40,
            'MR LIVER WO+W CONTRAST' : 30,
            'MRI LIVER MULTIPHASE' : 20,
            'MRI LIVER HCC SCREENING' : 10
    ])

    ContrastAgent contrastAgent = ContrastAgent.EXTRACELLULAR

    @Override
    List<SeriesType> getAllSeriesTypes() {
        final List<ContrastPhase> phases = [ContrastPhase.PRECONTRAST, ContrastPhase.LATE_ARTERIAL, ContrastPhase.PORTAL_VENOUS] +
            contrastAgent.phasesAfterPortalVenous
        [
                new Localizer(anatomicalPlane: AnatomicalPlane.CORONAL),
                new InOpposedPhaseT1(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new T2Weighted(anatomicalPlane: AnatomicalPlane.TRANSVERSE),
                new DiffusionWeightedImage(anatomicalPlane: AnatomicalPlane.TRANSVERSE)
        ] + phases.collect { phase ->
            new MultiphaseT1(anatomicalPlane: AnatomicalPlane.TRANSVERSE, phase: phase)
        } as List<SeriesType>
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.LIVER]
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        contrastAgent.agentName ? "multiphase liver MRI with ${contrastAgent.agentName}" : 'multiphase liver MRI'
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('mri liver w/wo')
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.MRI
    }

}
