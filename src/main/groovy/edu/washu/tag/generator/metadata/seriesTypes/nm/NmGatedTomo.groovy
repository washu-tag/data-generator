package edu.washu.tag.generator.metadata.seriesTypes.nm

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.NmScanType

class NmGatedTomo extends NmSeriesType {

    @Override
    NmImageType getImageType() {
        new NmImageType().scanType(NmScanType.GATED_TOMO)
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        'SGATEGate'
    }

}
