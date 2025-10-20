package edu.washu.tag.generator.metadata.patient

import ca.uhn.hl7v2.model.v281.datatype.CX
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

import java.util.concurrent.ThreadLocalRandom

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
trait PatientId {

    String idNumber

    CX encodeId(CX emptyDataStore) {
        emptyDataStore.getCx1_IDNumber().setValue(idNumber)
        emptyDataStore.getCx2_IdentifierCheckDigit().setValue(
            getIdentifierCheckDigit()
        )
        emptyDataStore.getCx3_CheckDigitScheme().setValue(
            getCheckDigitScheme()
        )
        getAssigningAuthority()?.toHd(emptyDataStore.getCx4_AssigningAuthority())
        emptyDataStore.getCx5_IdentifierTypeCode().setValue(getIdentifierTypeCode())
        getAssigningFacility()?.toHd(emptyDataStore.getCx6_AssigningFacility())
        emptyDataStore
    }

    @JsonIgnore
    abstract HierarchicDesignator getAssigningAuthority()

    @JsonIgnore
    abstract String getIdentifierTypeCode()

    @JsonIgnore
    abstract boolean isApplicableForStudy(Study study)

    @JsonIgnore
    String getIdentifierCheckDigit() {
        null
    }

    @JsonIgnore
    String getCheckDigitScheme() {
        null
    }

    @JsonIgnore
    HierarchicDesignator getAssigningFacility() {
        null
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
