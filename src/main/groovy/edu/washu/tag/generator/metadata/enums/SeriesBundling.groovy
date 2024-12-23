package edu.washu.tag.generator.metadata.enums

enum SeriesBundling {
    PRIMARY (0, 0),
    KO (120, 180),
    RTSTRUCT (1, 110),
    PR_MG (1, 15)

    final int minDaysPassed
    final int maxDaysPassed

    SeriesBundling(int minDays, int maxDays) {
        minDaysPassed = minDays
        maxDaysPassed = maxDays
    }
}
