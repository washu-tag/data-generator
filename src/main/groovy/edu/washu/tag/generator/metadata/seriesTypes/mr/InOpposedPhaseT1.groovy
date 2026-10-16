package edu.washu.tag.generator.metadata.seriesTypes.mr

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.GEOptimaMR450w
import edu.washu.tag.generator.metadata.scanners.PhilipsAchieva
import edu.washu.tag.generator.metadata.scanners.SiemensAvanto
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.concurrent.ThreadLocalRandom

/**
 * Dual-echo T1 (in-phase and opposed-phase) acquisition, encoded as a single series containing both echoes.
 * Descriptions follow naming seen in the TCIA TCGA-LIHC collection.
 */
class InOpposedPhaseT1 extends MrSeriesType {

    private static final Map<String, Integer> genericDescriptions = [
            'IP/OP' : 25,
            '%PLANE% IN/OUT PHASE' : 20,
            'IN/OUT PHASE %PLANE%' : 10
    ]
    private static final EnumeratedDistribution<String> siemensRandomizer = vendorRandomizer([
            't1_vibe_tra_bh_dixon_opp-in' : 25,
            't1 flash %PLANE% BH opp-in' : 20
    ])
    private static final EnumeratedDistribution<String> geRandomizer = vendorRandomizer([
            'Ax DualEcho FSPGR' : 25,
            'SPGR DUAL AX' : 15
    ])
    private static final EnumeratedDistribution<String> philipsRandomizer = vendorRandomizer([
            'dual_FFE_fast_BH' : 25,
            'mDIXON IP/OP' : 15
    ])
    private static final EnumeratedDistribution<String> otherRandomizer = RandomGenUtils.setupWeightedLottery(genericDescriptions)

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        switch (scanner) {
            case SiemensAvanto -> siemensRandomizer
            case GEOptimaMR450w -> geRandomizer
            case PhilipsAchieva -> philipsRandomizer
            default -> otherRandomizer
        }
    }

    @Override
    int producedInstanceCount() {
        2 * ThreadLocalRandom.current().nextInt(24, 49)
    }

    private static EnumeratedDistribution<String> vendorRandomizer(Map<String, Integer> vendorDescriptions) {
        RandomGenUtils.setupWeightedLottery(vendorDescriptions + genericDescriptions)
    }

}
