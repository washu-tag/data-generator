package edu.washu.tag.generator.ai.catalog.attribute

import org.apache.commons.lang3.RandomUtils

enum DiagnosisCodeDesignator {

    ICD_9 ('I9', 'ICD-9'),
    ICD_10 ('I10', 'ICD-10')

    final String hl7Representation
    final String fullIdentifier

    DiagnosisCodeDesignator(String hl7Representation, String fullIdentifier) {
        this.hl7Representation = hl7Representation
        this.fullIdentifier = fullIdentifier
    }

    static DiagnosisCodeDesignator randomize() {
        RandomUtils.insecure().randomDouble(0, 1) < 0.95 ? ICD_10 : ICD_9
    }

}