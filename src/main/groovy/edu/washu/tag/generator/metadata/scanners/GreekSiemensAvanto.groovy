package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.institutions.WestAthensGeneralHospital

class GreekSiemensAvanto extends SiemensAvanto {

    @Override
    Institution getInstitution() {
        new WestAthensGeneralHospital()
    }

    @Override
    String getStationName() {
        'MRC27136'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['syngo MR D12']
    }

    @Override
    String getDeviceSerialNumber() {
        'S-000423719840917111-D188'
    }

    @Override
    boolean supportsNonLatinCharacterSets() {
        true
    }

}
