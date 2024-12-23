package edu.washu.tag.generator.metadata.protocols

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.enums.contextGroups.PetRadiopharmaceutical
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

abstract class PetStudy extends Protocol {

    PetRadiopharmaceutical currentPharmaceutical
    int currentInjectionOffset
    double currentDosage
    private static final EnumeratedDistribution<PetRadiopharmaceutical> radiopharmaceuticalRandomizer = RandomGenUtils.setupWeightedLottery([
            (PetRadiopharmaceutical.FLUORODEOXYGLUCOSE_F_18_) : 90,
            (PetRadiopharmaceutical.PITTSBURGH_COMPOUND_B_C_11_) : 10
    ])

    @Override
    void resample(Patient patient) {
        currentPharmaceutical = radiopharmaceuticalRandomizer.sample()
        currentInjectionOffset = currentPharmaceutical.scanDelayTime + ThreadLocalRandom.current().nextInt(-120, 121)
        currentDosage = ThreadLocalRandom.current().nextDouble(470000000, 530000000)
    }

}
