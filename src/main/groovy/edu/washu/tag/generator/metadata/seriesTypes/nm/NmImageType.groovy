package edu.washu.tag.generator.metadata.seriesTypes.nm

import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.enums.NmScanType

class NmImageType {

    boolean original = true
    NmScanType scanType
    boolean transmissionActive = false

    NmImageType scanType(NmScanType scanType) {
        setScanType(scanType)
        this
    }

    NmImageType derived() {
        original = false
        this
    }

    NmImageType transmissionActive() {
        transmissionActive = true
        this
    }

    ImageType resolve() {
        new ImageType(original: original, otherValues: [scanType.dicomRepresentation, transmissionActive ? 'TRANSMISSION' : 'EMISSION'])
    }

}
