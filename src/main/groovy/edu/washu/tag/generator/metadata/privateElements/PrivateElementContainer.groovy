package edu.washu.tag.generator.metadata.privateElements

import org.dcm4che3.data.Attributes
import edu.washu.tag.generator.metadata.Patient

trait PrivateElementContainer {

    abstract List<PrivateBlock> scopeAppropriatePrivateElements()

    abstract List<? extends PrivateElementContainer> childObjects()

    void encodePrivateElements(Attributes attributes) {
        scopeAppropriatePrivateElements().each { privateBlock ->
            privateBlock.elements.each { privateElement ->
                privateElement.encode(
                        attributes,
                        privateBlock.privateCreatorId,
                        Integer.parseInt(privateElement.resolveToFriendlyTag(privateBlock), 16)
                )
            }
        }
    }

    void resolvePrivateElements(Patient patient, List<List<PrivateBlock>> previouslyResolved) {
        final List<PrivateBlock> currentScopeElements = scopeAppropriatePrivateElements()
        final List<List<PrivateBlock>> knownElements = new ArrayList<>(previouslyResolved)
        if (currentScopeElements) {
            knownElements << currentScopeElements
            final Map<String, List<String>> groupBlockCounter = [:]
            knownElements.each { elementList ->
                elementList.each { privateBlock ->
                    final String groupNumber = privateBlock.groupNumber
                    final List<String> alreadyReservedIds = groupBlockCounter.computeIfAbsent(groupNumber, (unused) -> [])
                    final int reservedIndex = alreadyReservedIds.indexOf(privateBlock.privateCreatorId)
                    if (reservedIndex == -1) {
                        alreadyReservedIds << privateBlock.privateCreatorId
                        privateBlock.reserveByIndex(alreadyReservedIds.size() - 1)
                    } else {
                        privateBlock.reserveByIndex(reservedIndex)
                    }
                }
            }
        }
        recursePrivateElementResolution(patient, knownElements)
    }

    void recursePrivateElementResolution(Patient patient, List<List<PrivateBlock>> knownElements) {
        childObjects().each { PrivateElementContainer child ->
            child.resolvePrivateElements(patient, knownElements)
        }
    }

}
