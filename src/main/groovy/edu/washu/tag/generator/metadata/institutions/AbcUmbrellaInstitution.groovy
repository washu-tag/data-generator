package edu.washu.tag.generator.metadata.institutions

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.patient.MainId

abstract class AbcUmbrellaInstitution extends Institution {

    @Override
    HierarchicDesignator assigningAuthority() {
        MainId.assigningAuthority
    }

}
