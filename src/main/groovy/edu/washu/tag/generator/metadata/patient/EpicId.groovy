package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

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

    @Override
    boolean isApplicableForStudy(Study study) {
        study.radReport.hl7Version in [ReportVersion.V2_4, ReportVersion.V2_7]
    }

}
