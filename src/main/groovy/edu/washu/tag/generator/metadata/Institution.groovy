package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.enums.Nationality

abstract class Institution {

    abstract String getInstitutionName()

    abstract String getInstitutionAddress()

    Nationality commonNationality() {
        Nationality.AMERICAN
    }

    HierarchicDesignator assigningAuthority() {
        null
    }

}
