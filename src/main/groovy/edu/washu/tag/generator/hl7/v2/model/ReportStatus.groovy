package edu.washu.tag.generator.hl7.v2.model

import org.apache.commons.lang3.RandomUtils

enum ReportStatus {

    PRELIMINARY ('P', 'Prelim', 'Preliminary'),
    FINAL ('F', 'Final', 'Final'),
    WET_READ (null, 'Wet Read', null)

    ReportStatus(String historical, String current, String full) {
        historicalText = historical
        currentText = current
        fullText = full
    }

    private final String historicalText
    private final String currentText
    private final String fullText

    String getHistoricalText() {
        historicalText
    }

    String getCurrentText() {
        currentText
    }

    String randomizeTitle() {
        final String title = "${fullText ?: PRELIMINARY.fullText} Report"
        RandomUtils.secure().randomBoolean() ? title : title.toUpperCase()
    }

}
