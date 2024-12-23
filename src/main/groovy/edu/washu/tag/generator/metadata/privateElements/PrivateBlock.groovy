package edu.washu.tag.generator.metadata.privateElements

import com.fasterxml.jackson.annotation.JsonIgnore

class PrivateBlock {

    String privateCreatorId
    String groupNumber // "gggg" in (gggg,xxee)
    @JsonIgnore String reservation // "xx" in (gggg,xxee)
    List<PrivateElement> elements
    public static final String CUSTOM_BLOCK_CREATOR = 'NRG Data Generator'
    public static final String CUSTOM_BLOCK_NUMBER = '0099'
    public static final int HEX_RADIX = 16

    void reserve(int reservationInt) {
        reservation = Integer.toString(reservationInt, HEX_RADIX)
    }

    void reserveByIndex(int index) {
        reserve(index + HEX_RADIX)
    }

    @JsonIgnore
    String padGroup() {
        groupNumber.padLeft(4, '0')
    }

    boolean sameGroup(PrivateBlock privateBlock) {
        padGroup() == privateBlock.padGroup()
    }

    static PrivateBlock selfReferential() {
        new PrivateBlock(
                privateCreatorId: CUSTOM_BLOCK_CREATOR,
                groupNumber: CUSTOM_BLOCK_NUMBER,
                elements: []
        )
    }

}
