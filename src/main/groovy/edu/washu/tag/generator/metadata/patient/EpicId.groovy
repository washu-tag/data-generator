package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator

class EpicId implements PatientId {

    private static final HierarchicDesignator assigningAuthority = HierarchicDesignator.simple('EPIC')

    @Override
    HierarchicDesignator getAssigningAuthority() {
        assigningAuthority
    }

    @Override
    String getIdentifierTypeCode() {
        'MRN'
    }

}
