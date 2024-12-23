package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.util.GenerationConstants

class MainId implements PatientId {

    public static final HierarchicDesignator assigningAuthority = HierarchicDesignator.simple(GenerationConstants.MAIN_HOSPITAL)

    @Override
    HierarchicDesignator getAssigningAuthority() {
        assigningAuthority
    }

    @Override
    String getIdentifierTypeCode() {
        'MR'
    }

}
