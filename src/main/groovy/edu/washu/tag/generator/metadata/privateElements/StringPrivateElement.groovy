package edu.washu.tag.generator.metadata.privateElements

import org.dcm4che3.data.Attributes

class StringPrivateElement extends PrivateElement<String> {

    @Override
    void encode(Attributes attributes, String privateCreator, int resolvedDestinationHeader) {
        attributes.setString(
                privateCreator,
                resolvedDestinationHeader,
                vr,
                value
        )
    }

}
