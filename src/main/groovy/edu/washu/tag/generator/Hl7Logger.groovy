package edu.washu.tag.generator

import edu.washu.tag.generator.util.TimeUtils
import org.apache.commons.lang3.RandomUtils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Hl7Logger {

    private static final String CR_REPLACEMENT = '<R>'
    private static final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern('uuuu-MM-dd HH:mm:ss.SSSS')

    void writeToHl7ishLogFiles(File hl7SourceDir) {
        final File logOutputDir = new File('hl7ish_logs')

        (hl7SourceDir.listFiles() as List<File>).each { yearDir ->
            final String year = yearDir.name
            final File logYearDir = new File(logOutputDir, year)
            logYearDir.mkdirs()
            (yearDir.listFiles() as List<File>).each { monthDir ->
                final String month = monthDir.name.padLeft(2, '0')
                (monthDir.listFiles() as List<File>).each { dayDir ->
                    final String day = dayDir.name.padLeft(2, '0')
                    final List<TimedMessage> messagesForDay = (dayDir.listFiles() as List<File>).findAll {
                        it.name.endsWith('.hl7')
                    }.collect { hl7File ->
                        new TimedMessage(
                                hl7File.text,
                                hl7File
                                        .name
                                        .dropRight(4) // drop .hl7
                                        .split('_')[1]
                        )
                    }
                    messagesForDay.sort { it.reportTime }

                    final File fileForDay = new File(logYearDir, "${year}${month}${day}.log")
                    if (fileForDay.exists()) {
                        fileForDay.delete()
                    }
                    fileForDay.createNewFile()
                    fileForDay.text = messagesForDay*.transform().join("${CR_REPLACEMENT}\n\r\n")
                }
            }
        }
    }

    private class TimedMessage {
        String hl7
        LocalDateTime reportTime

        TimedMessage(String hl7, String reportTime) {
            this.hl7 = hl7
            this.reportTime = TimeUtils.HL7_FORMATTER_DATETIME
                .parse(reportTime, LocalDateTime::from)
                .plus(100 * RandomUtils.insecure().randomInt(0, 9999), ChronoUnit.MICROS)
        }

        String transform() {
            final List<String> lines = [
                    "${timestampFormatter.format(reportTime)}|INFO|Raw.Application.TcpServer.TcpServer|",
                    '<SB>'
            ]
            hl7.split('\r').each {
                lines << it + CR_REPLACEMENT
            }
            lines << '<EB>'
            lines.join('\n')
        }
    }

}
