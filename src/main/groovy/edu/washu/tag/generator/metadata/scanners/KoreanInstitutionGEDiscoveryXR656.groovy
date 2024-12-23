package edu.washu.tag.generator.metadata.scanners


import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.institutions.SeoulInterventionalRadiologyClinic

class KoreanInstitutionGEDiscoveryXR656 extends GEDiscoveryXR656 {

    @Override
    Institution getInstitution() {
        new SeoulInterventionalRadiologyClinic()
    }

    @Override
    String getStationName() {
        'DXRAD_A'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['dm_Platform_release-FW15_1-1994']
    }

    @Override
    String getDeviceSerialNumber() {
        '208022LA1'
    }

    @Override
    boolean supportsNonLatinCharacterSets() {
        true
    }

}
