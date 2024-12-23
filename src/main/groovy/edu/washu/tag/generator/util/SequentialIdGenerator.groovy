package edu.washu.tag.generator.util

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import java.util.function.Supplier

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class SequentialIdGenerator implements Supplier<String> {

    private int currentId
    String prefix = ''
    String suffix = ''

    SequentialIdGenerator(int numDigits = RandomGenUtils.DEFAULT_NUM_DIGITS) {
        currentId = RandomGenUtils.randomId(numDigits)
    }

    @Override
    String get() {
        prefix + (currentId++) + suffix
    }

}
