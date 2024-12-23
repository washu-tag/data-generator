package edu.washu.tag.generator.metadata.institutions

import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.enums.Nationality

class SeoulInterventionalRadiologyClinic extends Institution {

    @Override
    String getInstitutionName() {
        'Seoul Interventional Radiology Clinic'
    }

    @Override
    String getInstitutionAddress() {
        '1484-9, Gayang 3(sam)-dong, Gangseo-gu, Seoul, Korea'
    }

    @Override
    Nationality commonNationality() {
        Nationality.KOREAN
    }

}
