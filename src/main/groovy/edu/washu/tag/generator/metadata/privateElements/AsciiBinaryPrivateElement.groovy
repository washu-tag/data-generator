package edu.washu.tag.generator.metadata.privateElements

import org.dcm4che3.data.Attributes

import java.nio.charset.StandardCharsets

class AsciiBinaryPrivateElement extends PrivateElement<String> {

    @Override
    void encode(Attributes attributes, String privateCreator, int resolvedDestinationHeader) {
        attributes.setBytes(
                privateCreator,
                resolvedDestinationHeader,
                vr,
                value.getBytes(StandardCharsets.US_ASCII)
        )
    }

}
