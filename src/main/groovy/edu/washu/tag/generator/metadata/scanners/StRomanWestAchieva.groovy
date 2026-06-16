package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Institutions

class StRomanWestAchieva extends PhilipsAchieva {

    @Override
    Institution getDefaultInstitution() {
        Institutions.stRomanWest
    }

    @Override
    String getStationName() {
        'PHILIPS-AA00005'
    }

    @Override
    String getDeviceSerialNumber() {
        '22528'
    }

}
