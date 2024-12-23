package edu.washu.tag.generator.metadata.enums

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.util.RandomGenUtils

enum AnatomicalPlane {

    TRANSVERSE (['AX' : 40, 'Ax' : 20, 'Tra' : 20, 'Axial' : 15]),
    CORONAL (['cor' : 30, 'COR' : 30, 'Coronal' : 20]),
    SAGITTAL (['SAG' : 50, 'sag' : 30, 'Sagittal' : 20])

    AnatomicalPlane(Map<String, Integer> valueRepresentationWeights) {
        encodedValueRandomizer = RandomGenUtils.setupWeightedLottery(valueRepresentationWeights)
    }

    private final EnumeratedDistribution<String> encodedValueRandomizer

    String sampleEncodedValue() {
        encodedValueRandomizer.sample()
    }

}