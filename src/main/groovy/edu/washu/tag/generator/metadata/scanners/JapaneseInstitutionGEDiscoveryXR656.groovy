package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.*
import edu.washu.tag.generator.metadata.institutions.HokkaidoRadiologyCenter

class JapaneseInstitutionGEDiscoveryXR656 extends GEDiscoveryXR656 {

    @Override
    Institution getInstitution() {
        new HokkaidoRadiologyCenter()
    }

    @Override
    String getStationName() {
        'DX1'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['dm_Platform_release-FW12_3-1994']
    }

    @Override
    String getDeviceSerialNumber() {
        '111472XV1'
    }

    @Override
    boolean supportsNonLatinCharacterSets() {
        true
    }

}
