package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.enums.Nationality
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ['id'])
class Institution {

    String id // used to override institution at runtime
    String institutionName
    String institutionAddress
    Nationality commonNationality = Nationality.AMERICAN
    HierarchicDesignator assigningAuthority

    String getId() {
        id ?: institutionName
    }

}
