package edu.washu.tag.generator.metadata.patient

import ca.uhn.hl7v2.model.v281.datatype.CX
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
trait PatientId {

    String idNumber

    CX encodeId(CX emptyDataStore) {
        emptyDataStore.getCx1_IDNumber().setValue(idNumber)
        getAssigningAuthority().toHd(emptyDataStore.getCx4_AssigningAuthority())
        emptyDataStore.getCx5_IdentifierTypeCode().setValue(getIdentifierTypeCode())
        emptyDataStore
    }

    @JsonIgnore
    abstract HierarchicDesignator getAssigningAuthority()

    @JsonIgnore
    abstract String getIdentifierTypeCode()

    @JsonIgnore
    String expectedColumnName() {
        getAssigningAuthority().namespaceId.toLowerCase() + '_' + getIdentifierTypeCode().toLowerCase()
    }

}
