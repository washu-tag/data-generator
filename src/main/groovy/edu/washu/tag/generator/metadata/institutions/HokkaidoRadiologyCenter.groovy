package edu.washu.tag.generator.metadata.institutions

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.enums.Nationality

class HokkaidoRadiologyCenter extends Institution {

    @Override
    String getInstitutionName() {
        'Hokkaido Radiology Center'
    }

    @Override
    String getInstitutionAddress() {
        '487-1010, Kita 7-jonishi, Ashibetsu-shi, Hokkaido, Japan'
    }

    @Override
    Nationality commonNationality() {
        Nationality.JAPANESE
    }

}
