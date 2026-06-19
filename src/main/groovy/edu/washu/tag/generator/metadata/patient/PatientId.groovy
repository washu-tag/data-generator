package edu.washu.tag.generator.metadata.patient

import ca.uhn.hl7v2.model.v281.datatype.CX
import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

import java.util.function.Predicate

class PatientId {

    String idNumber
    String identifierCheckDigit
    String checkDigitScheme
    HierarchicDesignator assigningAuthority
    String identifierTypeCode
    HierarchicDesignator assigningFacility
    @JsonIgnore Predicate<Study> checkApplicabilityFor = { true }

    CX encodeId(CX emptyDataStore) {
        emptyDataStore.getCx1_IDNumber().setValue(idNumber)
        emptyDataStore.getCx2_IdentifierCheckDigit().setValue(
            identifierCheckDigit
        )
        emptyDataStore.getCx3_CheckDigitScheme().setValue(
            checkDigitScheme
        )
        assigningAuthority?.toHd(emptyDataStore.getCx4_AssigningAuthority())
        emptyDataStore.getCx5_IdentifierTypeCode().setValue(identifierTypeCode)
        assigningFacility?.toHd(emptyDataStore.getCx6_AssigningFacility())
        emptyDataStore
    }

    @JsonIgnore
    String expectedColumnName() {
        final HierarchicDesignator authority = getAssigningAuthority()
        if (authority != null) {
            authority.namespaceId.toLowerCase() + '_' + getIdentifierTypeCode().toLowerCase()
        } else {
            "legacy_patient_id_${getAssigningFacility().namespaceId.toLowerCase()}"
        }
    }

}
