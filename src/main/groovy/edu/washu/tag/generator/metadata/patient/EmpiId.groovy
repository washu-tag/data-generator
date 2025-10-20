package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

class EmpiId implements PatientId {

    public static final HierarchicDesignator assigningAuthority = HierarchicDesignator.simple('EMPI')

    @Override
    HierarchicDesignator getAssigningAuthority() {
        assigningAuthority
    }

    @Override
    String getIdentifierTypeCode() {
        'MR'
    }

    @Override
    boolean isApplicableForStudy(Study study) {
        false // this will be conditionally inserted in CyclicVariedGenerator
    }

}
