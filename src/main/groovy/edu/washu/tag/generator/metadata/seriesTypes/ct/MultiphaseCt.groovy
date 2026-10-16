package edu.washu.tag.generator.metadata.seriesTypes.ct

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.ContrastPhase
import edu.washu.tag.generator.metadata.pixels.CachedPixelSpec
import edu.washu.tag.generator.metadata.pixels.ZippedCachedPixelSpec
import edu.washu.tag.generator.metadata.scanners.CtScanner
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64
import edu.washu.tag.generator.metadata.seriesTypes.ContrastPhased
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.StringReplacements
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.concurrent.ThreadLocalRandom

/**
 * One phase of a diagnostic multiphase abdominal CT. Descriptions follow naming seen in the TCIA TCGA-LIHC collection:
 * Siemens series name the phase, slice thickness, and reconstruction kernel, while other vendors usually name just the phase.
 */
class MultiphaseCt extends CtSeriesType implements ContrastPhased {

    private static final EnumeratedDistribution<Double> sliceThicknesses = RandomGenUtils.setupWeightedLottery([
            (2.5) : 20,
            (3.0) : 60,
            (5.0) : 20
    ]) as EnumeratedDistribution<Double>

    private static final EnumeratedDistribution<String> siemensKernels = RandomGenUtils.setupWeightedLottery([
            'B40f' : 70,
            'B30f' : 30
    ])

    private static final EnumeratedDistribution<String> siemensRandomizer = RandomGenUtils.setupWeightedLottery([
            ("%PHASE%  ${StringReplacements.SLICE_THICKNESS}  ${StringReplacements.CONVOLUTION_KERNEL}".toString()) : 70,
            ("Liver %PHASE%  ${StringReplacements.SLICE_THICKNESS}  ${StringReplacements.CONVOLUTION_KERNEL}".toString()) : 15,
            '%PHASE%' : 15
    ])

    private static final EnumeratedDistribution<String> otherRandomizer = RandomGenUtils.setupWeightedLottery([
            '%PHASE%' : 60,
            '%PHASE% LIVER' : 20,
            '%PHASE% Abd' : 20
    ])

    ContrastPhase phase

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        (scanner instanceof SiemensBiographTruePoint64 ? siemensRandomizer : otherRandomizer).sample()
            .replace('%PHASE%', phase.sampleCtLabel())
    }

    @Override
    Double sampleSliceThickness(Equipment equipment) {
        sliceThicknesses.sample()
    }

    @Override
    String sampleConvolutionKernel(Equipment equipment) {
        equipment instanceof SiemensBiographTruePoint64 ? siemensKernels.sample() : null
    }

    @Override
    ImageType resolveImageType(CtScanner equipment) {
        equipment.getCtImageType(false)
    }

    @Override
    int producedInstanceCount() {
        ThreadLocalRandom.current().nextInt(60, 161)
    }

    @Override
    CachedPixelSpec pixelSpecFor(Series series, Instance instance) {
        ZippedCachedPixelSpec.ofRsnaTestData('PHILIPS/CT_SR/ABDOM_1') // contrast-enhanced axial through liver, gallbladder, and kidneys
    }

}
