package edu.washu.tag.generator.metadata.privateElements

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.VR

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property='@type')
abstract class PrivateElement<T> {

    VR vr
    String elementNumber // "ee" in (gggg,xxee)
    T value
    String meaning // nonstandard. Used for a custom private element to explain how to interpret inserted elements
    private String resolvedFriendlyTag

    abstract void encode(Attributes attributes, String privateCreator, int resolvedDestinationHeader)

    // e.g. concatenate "0029" + "10" + "20" to mean 0x00291020
    String resolveToFriendlyTag(PrivateBlock privateBlock) {
        if (!resolvedFriendlyTag) {
            resolvedFriendlyTag = pad(privateBlock.groupNumber, 4) + privateBlock.reservation + pad(elementNumber, 2)
        }
        resolvedFriendlyTag
    }

    String renderTag(PrivateBlock privateBlock) {
        resolveToFriendlyTag(privateBlock)
        "(${resolvedFriendlyTag.substring(0, 4)},${resolvedFriendlyTag.substring(4)})"
    }

    private String pad(String input, int numChars) {
        input.padLeft(numChars, '0')
    }

}
