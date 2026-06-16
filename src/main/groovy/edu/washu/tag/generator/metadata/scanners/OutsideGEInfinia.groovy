package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Institutions

class OutsideGEInfinia extends GEInfinia {

    @Override
    Institution getDefaultInstitution() {
        Institutions.centerForSpecializedRadiology
    }

    @Override
    String getStationName() {
        'MPI-NM-01'
    }

    @Override
    String getDeviceSerialNumber() {
        '12911'
    }

}
