package edu.washu.tag.generator.metadata.seriesTypes.nm

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.NmScanType

class MyometrixResults extends NmSeriesType {

    @Override
    NmImageType getImageType() {
        new NmImageType().scanType(NmScanType.RECON_TOMO).derived()
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        'Myometrix Results'
    }

}