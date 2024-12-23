package edu.washu.tag.generator.metadata.privateElements

import com.fasterxml.jackson.annotation.JsonIgnore

class AsciiRepeatedBinaryPrivateElement extends AsciiBinaryPrivateElement {

    public static final String EXPLANATION = 'nonsense string repeated multiple times: '
    public static final String NONSENSE = '1234567890'

    int repetitionCount

    @Override
    @JsonIgnore
    String getValue() {
        EXPLANATION + (NONSENSE * repetitionCount)
    }

}
