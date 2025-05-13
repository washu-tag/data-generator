package edu.washu.tag.generator

import edu.washu.tag.generator.util.TimeUtils
import org.apache.commons.lang3.RandomUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Hl7Logger {

    private static final String CR_REPLACEMENT = '<R>'
    private static final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern('uuuu-MM-dd HH:mm:ss.SSSS')
    private static final Logger logger = LoggerFactory.getLogger(Hl7Logger)

    List<Hl7LogFile> identifyHl7LogFiles(File hl7SourceDir) {
        final File logOutputDir = new File('hl7ish_logs')

        (hl7SourceDir.listFiles() as List<File>).collectMany { yearDir ->
            final String year = yearDir.name
            final File logYearDir = new File(logOutputDir, year)
            logYearDir.mkdirs()
            (yearDir.listFiles() as List<File>).collectMany { monthDir ->
                final String month = monthDir.name.padLeft(2, '0')
                (monthDir.listFiles() as List<File>).collect { dayDir ->
                    final String day = dayDir.name.padLeft(2, '0')

                    new Hl7LogFile(
                        year: year,
                        month: month,
                        day: day,
                        dayDir: dayDir,
                        asFile: new File(logYearDir, "${year}${month}${day}.log")
                    )
                }
            }
        }
    }

    void writeToHl7ishLogFile(Hl7LogFile logFileToWrite, boolean overwriteExisting = true) {
        final File fileForDay = logFileToWrite.asFile
        if (fileForDay.exists()) {
            if (overwriteExisting) {
                fileForDay.delete()
            } else {
                return
            }
        }
        fileForDay.createNewFile()

        logger.info('Reading hl7 files in {}...', logFileToWrite.dayDir.absolutePath)
        final List<TimedMessage> messagesForDay = (logFileToWrite.dayDir.listFiles() as List<File>).findAll {
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
        logger.info('Read hl7 files in {}', logFileToWrite.dayDir.absolutePath)
        messagesForDay.sort { it.reportTime }

        fileForDay.text = messagesForDay*.transform().join("${CR_REPLACEMENT}\n\r\n")
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
