package edu.washu.tag.generator.metadata.enums

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.util.RandomGenUtils

/**
 * The goal of this enum is not to model exactly what a "race" is, but rather to reflect the reality
 * of what ends up in a clinical PACS or HL7 feed in those fields
 */
enum Race {

    AMERICAN_INDIAN (['American Indian' : 100], 'I', 'AMIND'),
    ASIAN (['ASIAN' : 50, 'Asian' : 30, 'A' : 20], 'A', 'ASIAN'),
    BLACK (['B' : 55, 'BLACK' : 40, 'Black' : 3, 'BLACK OR AFRICAN' : 2], 'B', 'BLACK'),
    OTHER (['OTHER' : 40, 'O' : 30, 'Other' : 30], 'R', 'OTHER'),
    PACIFIC_ISLANDER (['Pacific Islander' : 100], 'P', 'PCFIS'),
    WHITE (['C' : 33, 'WHITE' : 25, 'W' : 25, 'CAU' : 17], 'C', 'WHITE'),
    UNABLE_TO_PROVIDE (null, 'U', 'UNABLE'),
    DECLINED_TO_PROVIDE (null, 'U', 'DECLINED')

    Race(Map<String, Integer> valueRepresentationWeights, String v24, String v27) {
        if (valueRepresentationWeights != null) {
            encodedValueRandomizer = RandomGenUtils.setupWeightedLottery(valueRepresentationWeights)
        } else {
            encodedValueRandomizer = null
        }
        hl7v24Encoding = v24
        hl7v27Encoding = v27
    }

    private final EnumeratedDistribution<String> encodedValueRandomizer
    private final String hl7v24Encoding
    private final String hl7v27Encoding

    String sampleEncodedValue() {
        encodedValueRandomizer.sample()
    }

    String getHl7v24Encoding() {
        hl7v24Encoding
    }

    String getHl7v27Encoding() {
        hl7v27Encoding
    }

}
