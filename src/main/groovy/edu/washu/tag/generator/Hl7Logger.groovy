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
        final File logOutputDir = BatchProcessor.logOutput

        // HL7 files are written into per-batch subtrees (hl7/batch_<id>/<year>/<month>/<day>/) so a Temporal
        // retry can wipe and rewrite a single batch in isolation. Collapse those subtrees back into one log
        // per calendar day, gathering every batch directory that wrote to that day.
        final Map<List<String>, List<File>> dayDirsByDate = [:].withDefault { [] }
        (hl7SourceDir.listFiles(File::isDirectory as FileFilter) as List<File>).each { batchDir ->
            (batchDir.listFiles(File::isDirectory as FileFilter) as List<File>).each { yearDir ->
                final String year = yearDir.name
                (yearDir.listFiles(File::isDirectory as FileFilter) as List<File>).each { monthDir ->
                    final String month = monthDir.name.padLeft(2, '0')
                    (monthDir.listFiles(File::isDirectory as FileFilter) as List<File>).each { dayDir ->
                        final String day = dayDir.name.padLeft(2, '0')
                        dayDirsByDate[[year, month, day]] << dayDir
                    }
                }
            }
        }

        dayDirsByDate.collect { dateKey, dayDirs ->
            final String year = dateKey[0]
            final File logYearDir = new File(logOutputDir, year)
            logYearDir.mkdirs()
            new Hl7LogFile(
                year: year,
                month: dateKey[1],
                day: dateKey[2],
                dayDirs: dayDirs,
                asFile: new File(logYearDir, "${year}${dateKey[1]}${dateKey[2]}.log")
            )
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

        final List<TimedMessage> messagesForDay = logFileToWrite.dayDirs.collectMany { dayDir ->
            logger.info('Reading hl7 files in {}...', dayDir.absolutePath)
            final List<TimedMessage> messages = (dayDir.listFiles() as List<File>).findAll {
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
            logger.info('Read hl7 files in {}', dayDir.absolutePath)
            messages
        }
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
