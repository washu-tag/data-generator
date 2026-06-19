package edu.washu.tag.generator.metadata.cohorting

import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.lang3.RandomUtils

import java.util.function.Function

enum CodeStrategy {

    ONLY_ONE ({ List<String> codes ->
        RandomGenUtils.randomListEntry(codes) }
    ),
    STRICT_SUBSET ({ List<String> codes ->
        RandomGenUtils.randomSubset(codes, RandomUtils.insecure().randomInt(1, codes.size()))
    }),
    SUBSET ({ List<String> codes ->
        RandomGenUtils.randomSubset(codes, RandomUtils.insecure().randomInt(1, codes.size() + 1))
    })

    private final Function<List<String>, List<String>> codeRandomizer

    CodeStrategy(Function<List<String>, List<String>> randomizer) {
        codeRandomizer = randomizer
    }

}