package edu.washu.tag.generator.ai.catalog.builder

import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator

class ModernReportTextBuilder extends ReportTextBuilder<ObxGenerator, ModernReportTextBuilder> {

    boolean includeDictation = true

    ModernReportTextBuilder() {
        super(ObxGenerator::forGeneralDescription)
    }

    void beginImpression() {
        currentSectionGenerator = ObxGenerator::forImpression
    }

    void disableDictationSection() {
        includeDictation = false
    }

}
