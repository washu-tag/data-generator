package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.institutions.CenterForSpecializedRadiology

class OutsideGEInfinia extends GEInfinia {

    @Override
    Institution getInstitution() {
        new CenterForSpecializedRadiology()
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
