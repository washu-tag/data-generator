package edu.washu.tag.generator.hl7.v2.model

import ca.uhn.hl7v2.model.v281.datatype.HD
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class HierarchicDesignator {

    String namespaceId
    String universalId
    String universalIdType

    HD toHd(HD emptyDataStore) {
        emptyDataStore.getHd1_NamespaceID().setValue(namespaceId)
        emptyDataStore.getHd2_UniversalID().setValue(universalId)
        emptyDataStore.getHd3_UniversalIDType().setValue(universalIdType)
        emptyDataStore
    }

    static HierarchicDesignator simple(String id) {
        new HierarchicDesignator().namespaceId(id)
    }

}
