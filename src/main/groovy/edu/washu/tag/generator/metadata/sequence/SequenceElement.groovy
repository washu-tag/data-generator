package edu.washu.tag.generator.metadata.sequence

import org.dcm4che3.data.Attributes

interface SequenceElement {

    void addToAttributes(Attributes attributes)

}