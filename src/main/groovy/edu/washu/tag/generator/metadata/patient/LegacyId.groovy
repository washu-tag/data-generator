package edu.washu.tag.generator.metadata.patient

import ca.uhn.hl7v2.model.v281.datatype.CX
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

import java.util.concurrent.ThreadLocalRandom

class LegacyId implements PatientId {

    @Override
    HierarchicDesignator getAssigningAuthority() {
        null
    }

    @Override
    String getIdentifierTypeCode() {
        null
    }

    @Override
    boolean isApplicableForStudy(Study study) {
        study.radReport.hl7Version == ReportVersion.V2_3
    }

    @Override
    CX encodeId(CX emptyDataStore) {
        emptyDataStore.getCx1_IDNumber().setValue(idNumber)
        emptyDataStore.getCx2_IdentifierCheckDigit().setValue(
            String.valueOf(ThreadLocalRandom.current().nextInt(1, 10))
        )
        emptyDataStore.getCx3_CheckDigitScheme().setValue(
            String.valueOf(ThreadLocalRandom.current().nextInt(1, 10))
        )
        emptyDataStore.getCx6_AssigningFacility().getHd1_NamespaceID().setValue('UN')
        emptyDataStore
    }

}
