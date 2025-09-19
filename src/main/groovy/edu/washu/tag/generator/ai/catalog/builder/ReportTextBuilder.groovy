package edu.washu.tag.generator.ai.catalog.builder

import edu.washu.tag.generator.metadata.RadiologyReport
import org.apache.commons.lang3.StringUtils

import java.util.function.BiFunction

abstract class ReportTextBuilder<T, S extends ReportTextBuilder<T, S>> {

    RadiologyReport radiologyReport
    BiFunction<String, RadiologyReport, List<T>> currentSectionGenerator
    List<T> textGenerators = []

    ReportTextBuilder(RadiologyReport radiologyReport, BiFunction<String, RadiologyReport, List<T>> defaultSectionGenerator) {
        this.radiologyReport = radiologyReport
        currentSectionGenerator = defaultSectionGenerator
    }

    S add(List<T> obxGenerators, int insertionIndex = textGenerators.size()) {
        textGenerators.addAll(insertionIndex, obxGenerators)
        this as S
    }

    S add(T obxGenerator, int insertionIndex = textGenerators.size()) {
        add([obxGenerator], insertionIndex)
    }

    S add(String content, int insertionIndex = textGenerators.size()) {
        add(currentSectionGenerator.apply(content, radiologyReport), insertionIndex)
    }

    S addSection(String sectionHeader, String sectionContent, SectionInternalDelimiter delimiter = SectionInternalDelimiter.NEWLINE, int insertionIndex = textGenerators.size()) {
        if (!textGenerators.isEmpty()) {
            add('')
        }
        add("${StringUtils.appendIfMissing(sectionHeader, ':')}${delimiter.delimiter}${sectionContent}", insertionIndex)
    }

}
