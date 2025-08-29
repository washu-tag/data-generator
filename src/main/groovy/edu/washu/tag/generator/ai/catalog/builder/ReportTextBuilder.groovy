package edu.washu.tag.generator.ai.catalog.builder

import org.apache.commons.lang3.StringUtils

import java.util.function.Function

abstract class ReportTextBuilder<T, S extends ReportTextBuilder<T, S>> {

    Function<String, List<T>> currentSectionGenerator
    List<T> textGenerators = []

    ReportTextBuilder(Function<String, List<T>> defaultSectionGenerator) {
        currentSectionGenerator = defaultSectionGenerator
    }

    S add(List<T> obxGenerators) {
        textGenerators.addAll(obxGenerators)
        this
    }

    S add(T obxGenerator) {
        add([obxGenerator])
    }

    S add(String content) {
        add(currentSectionGenerator.apply(content))
    }

    S addSection(String sectionHeader, String sectionContent, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE) {
        if (!textGenerators.isEmpty()) {
            add('')
        }
        add("${StringUtils.appendIfMissing(sectionHeader, ':')}${delimiter.delimiter}${sectionContent}")
    }

}
