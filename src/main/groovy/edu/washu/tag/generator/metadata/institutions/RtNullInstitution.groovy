package edu.washu.tag.generator.metadata.institutions

import edu.washu.tag.generator.metadata.Institution

class RtNullInstitution extends Institution {

    @Override
    String getInstitutionName() {
        null
    }

    @Override
    String getInstitutionAddress() {
        null
    }

}
