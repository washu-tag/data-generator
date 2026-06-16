package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Institutions

class VandeventerRegionalHospitalAvanto extends SiemensAvanto {

    @Override
    Institution getDefaultInstitution() {
        Institutions.vandeventerRegionalHospital
    }

    @Override
    String getStationName() {
        'MRC70990'
    }

    @Override
    String getDeviceSerialNumber() {
        'S-000431249820451709-B098'
    }

}
