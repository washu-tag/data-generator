package edu.washu.tag.generator.metadata

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR

class CodedTriplet {

    String codeValue
    String codingSchemeDesignator
    String codeMeaning
    String alternateText

    CodedTriplet(String value, String designator, String meaning, String alternateText = null) {
        codeValue = value
        codingSchemeDesignator = designator
        codeMeaning = meaning
        this.alternateText = alternateText
    }

    CodedTriplet() {

    }

    Attributes toSequenceItem() {
        final Attributes attributes = new Attributes()
        attributes.setString(Tag.CodeValue, VR.SH, codeValue)
        attributes.setString(Tag.CodingSchemeDesignator, VR.SH, codingSchemeDesignator)
        attributes.setString(Tag.CodeMeaning, VR.LO, codeMeaning)
        attributes
    }

}
