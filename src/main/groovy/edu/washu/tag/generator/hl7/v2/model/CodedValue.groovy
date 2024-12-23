package edu.washu.tag.generator.hl7.v2.model

import ca.uhn.hl7v2.model.v281.datatype.CWE

class CodedValue {

    String identifier
    String text
    String nameOfCodingSystem
    String alternateIdentifier
    String alternateText
    String nameOfAlternateCodingSystem
    String codingSystemVersionId
    String alternateCodingSystemVersionId
    String originalText
    String secondAlternateIdentifier
    String secondAlternateText
    String nameOfSecondAlternateCodingSystem
    String secondAlternateCodingSystemVersionId
    String codingSystemOid
    String valueSetOid
    String valueSetVersionId
    String alternateCodingSystemOid
    String alternateValueSetOid
    String alternateValueSetVersionId
    String secondAlternateCodingSystemOid
    String secondAlternateValueSetOid
    String secondAlternateValueSetVersionId

    CWE toCwe(CWE emptyDataStore) {
        emptyDataStore.getCwe1_Identifier().setValue(identifier)
        emptyDataStore.getCwe2_Text().setValue(text)
        emptyDataStore.getCwe3_NameOfCodingSystem().setValue(nameOfCodingSystem)
        emptyDataStore.getCwe4_AlternateIdentifier().setValue(alternateIdentifier)
        emptyDataStore.getCwe5_AlternateText().setValue(alternateText)
        emptyDataStore.getCwe6_NameOfAlternateCodingSystem().setValue(nameOfAlternateCodingSystem)
        emptyDataStore.getCwe7_CodingSystemVersionID().setValue(codingSystemVersionId)
        emptyDataStore.getCwe8_AlternateCodingSystemVersionID().setValue(alternateCodingSystemVersionId)
        emptyDataStore.getCwe9_OriginalText().setValue(originalText)
        emptyDataStore.getCwe10_SecondAlternateIdentifier().setValue(secondAlternateIdentifier)
        emptyDataStore.getCwe11_SecondAlternateText().setValue(secondAlternateText)
        emptyDataStore.getCwe12_NameOfSecondAlternateCodingSystem().setValue(nameOfSecondAlternateCodingSystem)
        emptyDataStore.getCwe13_SecondAlternateCodingSystemVersionID().setValue(secondAlternateCodingSystemVersionId)
        emptyDataStore.getCwe14_CodingSystemOID().setValue(codingSystemOid)
        emptyDataStore.getCwe15_ValueSetOID().setValue(valueSetOid)
        emptyDataStore.getCwe16_ValueSetVersionID().setValue(valueSetVersionId)
        emptyDataStore.getCwe17_AlternateCodingSystemOID().setValue(alternateCodingSystemOid)
        emptyDataStore.getCwe18_AlternateValueSetOID().setValue(alternateValueSetOid)
        emptyDataStore.getCwe19_AlternateValueSetVersionID().setValue(alternateValueSetVersionId)
        emptyDataStore.getCwe20_SecondAlternateCodingSystemOID().setValue(secondAlternateCodingSystemOid)
        emptyDataStore.getCwe21_SecondAlternateValueSetOID().setValue(secondAlternateValueSetOid)
        emptyDataStore.getCwe22_SecondAlternateValueSetVersionID().setValue(secondAlternateValueSetVersionId)
        emptyDataStore
    }

}
