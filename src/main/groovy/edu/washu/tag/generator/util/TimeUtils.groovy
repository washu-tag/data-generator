package edu.washu.tag.generator.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeUtils {

    public static final DateTimeFormatter HL7_FORMATTER_DATETIME = DateTimeFormatter.ofPattern('uuuuMMddHHmmss')
    public static final DateTimeFormatter HL7_FORMATTER_DATE = DateTimeFormatter.ofPattern('uuuuMMdd')
    public static final DateTimeFormatter UNAMBIGUOUS_DATE = DateTimeFormatter.ofPattern('uuuu-MM-dd')

    static String toHl7(LocalDateTime timestamp) {
        HL7_FORMATTER_DATETIME.format(timestamp)
    }

    static String toHl7(LocalDate timestamp) {
        HL7_FORMATTER_DATE.format(timestamp)
    }
    
}
