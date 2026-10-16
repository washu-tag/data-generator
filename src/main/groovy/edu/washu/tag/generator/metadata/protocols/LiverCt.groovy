package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.ContrastPhase
import edu.washu.tag.generator.metadata.seriesTypes.ct.MultiphaseCt
import edu.washu.tag.generator.metadata.seriesTypes.ct.Topogram
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.concurrent.ThreadLocalRandom

/**
 * Multiphase contrast-enhanced liver CT, as used for LI-RADS diagnostic imaging. LI-RADS requires the late arterial,
 * portal venous, and delayed phases; the precontrast phase is only suggested, so it is included in a fraction of studies.
 */
class LiverCt extends Protocol {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'CT ABDOMEN W WO CONTRAST LIVER PROTOCOL' : 40,
            'CT LIVER MULTIPHASE' : 30,
            'CT ABD TRIPHASIC LIVER' : 20,
            'CT LIVER 4 PHASE' : 10
    ])

    double precontrastFraction = 0.6

    @Override
    void resample(Patient patient) {
        setSeriesTypes(getAllSeriesTypes())
    }

    @Override
    List<SeriesType> getAllSeriesTypes() {
        final List<ContrastPhase> phases = [ContrastPhase.LATE_ARTERIAL, ContrastPhase.PORTAL_VENOUS, ContrastPhase.DELAYED]
        if (ThreadLocalRandom.current().nextDouble() < precontrastFraction) {
            phases.add(0, ContrastPhase.PRECONTRAST)
        }
        [new Topogram()] + phases.collect { phase ->
            new MultiphaseCt(phase: phase)
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
        'multiphase liver CT'
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('ct liver multiphase')
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.CT
    }

}
