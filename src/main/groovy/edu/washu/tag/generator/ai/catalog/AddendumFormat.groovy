package edu.washu.tag.generator.ai.catalog

import org.apache.commons.lang3.RandomUtils

enum AddendumFormat {

    STANDARD ('ADDENDUM: '),
    WITH_ASTERISKS ('**ADDENDUM**\n\n'),
    STANDALONE ('')

    final String prefix

    AddendumFormat(String prefix) {
        this.prefix = prefix
    }

    String format(String addendum) {
        "${prefix}${addendum}"
    }

    static AddendumFormat randomize(boolean is2_4) {
        if (is2_4) {
            RandomUtils.insecure().randomBoolean() ? STANDARD : WITH_ASTERISKS
        } else {
            RandomUtils.insecure().randomDouble(0, 1) < 0.90 ? STANDARD : STANDALONE
        }
    }

}