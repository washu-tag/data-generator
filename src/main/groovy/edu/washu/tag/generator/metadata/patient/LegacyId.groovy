package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

import java.util.concurrent.ThreadLocalRandom

class LegacyId implements PatientId {

    public static final HierarchicDesignator facility = HierarchicDesignator.simple('UN')

    @Override
    HierarchicDesignator getAssigningAuthority() {
        null
    }

    @Override
    String getIdentifierCheckDigit() {
        String.valueOf(ThreadLocalRandom.current().nextInt(1, 10))
    }

    @Override
    String getCheckDigitScheme() {
        String.valueOf(ThreadLocalRandom.current().nextInt(1, 10))
    }

    @Override
    String getIdentifierTypeCode() {
        null
    }

    @Override
    HierarchicDesignator getAssigningFacility() {
        facility
    }

    @Override
    boolean isApplicableForStudy(Study study) {
        study.radReport.hl7Version == ReportVersion.V2_3
    }

}
