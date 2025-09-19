package edu.washu.tag.generator.ai.catalog.builder

import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.metadata.RadiologyReport

class ModernReportTextBuilder extends ReportTextBuilder<ObxGenerator, ModernReportTextBuilder> {

    boolean includeDictation = true

    ModernReportTextBuilder(RadiologyReport radReport) {
        super(radReport, ObxGenerator::forGeneralDescription)
    }

    void beginAddendum() {
        currentSectionGenerator = ObxGenerator::forAddendum
    }

    void beginGeneralDescription() {
        currentSectionGenerator = ObxGenerator::forGeneralDescription
    }

    void beginImpression() {
        currentSectionGenerator = ObxGenerator::forImpression
    }

    void beginTechnicianNote() {
        currentSectionGenerator = ObxGenerator::forTechnicianNote
    }

    void disableDictationSection() {
        includeDictation = false
    }

}
