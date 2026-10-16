package edu.washu.tag.generator.metadata.seriesTypes.mr

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.ContrastPhase
import edu.washu.tag.generator.metadata.scanners.GEOptimaMR450w
import edu.washu.tag.generator.metadata.scanners.PhilipsAchieva
import edu.washu.tag.generator.metadata.scanners.SiemensAvanto
import edu.washu.tag.generator.metadata.seriesTypes.ContrastPhased
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.concurrent.ThreadLocalRandom

/**
 * One phase of a fat-saturated, breath-hold dynamic T1 gradient-echo acquisition as used in liver MRI. Descriptions
 * follow vendor sequence naming seen in the TCIA TCGA-LIHC collection (VIBE, LAVA, mDIXON).
 */
class MultiphaseT1 extends MrSeriesType implements ContrastPhased {

    private static final Map<String, Integer> genericDescriptions = [
            '%PLANE% T1 FS %PHASE%' : 15,
            'T1 FS BH %PLANE% %PHASE%' : 10
    ]
    private static final EnumeratedDistribution<String> siemensRandomizer = vendorRandomizer([
            'AX T1 VIBE FS %PHASE%' : 35,
            't1_vibe_fs_tra_bh_%PHASE%' : 25,
            'T1 VIBE FS TRA_%PHASE%' : 15
    ])
    private static final EnumeratedDistribution<String> geRandomizer = vendorRandomizer([
            'lava %PHASE%' : 35,
            'Ax LAVA BH %PHASE%' : 25,
            'lava3d %PHASE%' : 15
    ])
    private static final EnumeratedDistribution<String> philipsPrecontrastRandomizer = vendorRandomizer([
            'mDIXON-W %PHASE%' : 75
    ])
    private static final EnumeratedDistribution<String> philipsPostcontrastRandomizer = vendorRandomizer([
            'mDIXON-W %PHASE%' : 40,
            'mDIXON +C %PHASE%' : 35 // "+C" marks post-contrast, so it is never used for the precontrast phase
    ])
    private static final EnumeratedDistribution<String> otherRandomizer = RandomGenUtils.setupWeightedLottery(genericDescriptions)

    ContrastPhase phase

    @Override
    EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner) {
        switch (scanner) {
            case SiemensAvanto -> siemensRandomizer
            case GEOptimaMR450w -> geRandomizer
            case PhilipsAchieva -> phase == ContrastPhase.PRECONTRAST ? philipsPrecontrastRandomizer : philipsPostcontrastRandomizer
            default -> otherRandomizer
        }
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        super.getSeriesDescription(scanner, bodyPartExamined).replace('%PHASE%', phase.sampleMrLabel())
    }

    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(64, 129)
    }

    private static EnumeratedDistribution<String> vendorRandomizer(Map<String, Integer> vendorDescriptions) {
        RandomGenUtils.setupWeightedLottery(vendorDescriptions + genericDescriptions)
    }

}
