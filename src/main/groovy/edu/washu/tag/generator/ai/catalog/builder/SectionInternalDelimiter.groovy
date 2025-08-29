package edu.washu.tag.generator.ai.catalog.builder

enum SectionInternalDelimiter {

    SPACE (' '),
    NEWLINE ('\n'),
    SPACE_NEWLINE (' \n'),
    DOUBLE_NEWLINE ('\n\n')

    String delimiter

    SectionInternalDelimiter(String delimiter) {
        this.delimiter = delimiter
    }

}