package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

import java.nio.file.Path
import java.time.LocalDateTime

class OutputHandler {

    Path batchDirectory
    Path hl7Directory
    private int layerId = -1
    private int fileId
    private int patientsWritten = 0
    private Path layer

    OutputHandler(int id) {
        batchDirectory = BatchProcessor.dicomOutput.toPath().resolve("batch_${id}")
        batchDirectory.toFile().mkdir()
        hl7Directory = BatchProcessor.hl7Output.toPath()
    }

    void writeDicomForPatient(Patient patient) {
        if (patientsWritten % BatchSpecification.MAX_PATIENTS_PER_LAYER == 0) {
            layerId++
            fileId = 1
            layer = batchDirectory.resolve("layer_${layerId}")
            layer.toFile().mkdir()
        }
        patient.studies.each { study ->
            final Path studyPath = layer.resolve(study.studyInstanceUid)
            studyPath.toFile().mkdir()
            if (patient.race != null) {
                study.setEthnicGroup(patient.race.sampleEncodedValue())
            }
            study.resolvePrivateElements(patient, [])
            study.series.each { series ->
                series.instances.each { instance ->
                    instance.writeToDicomFile(patient, study, series, studyPath.resolve("${fileId}.dcm").toFile())
                    fileId++
                }
            }
        }
        patientsWritten++
    }

    void writeHl7ForPatient(Patient patient) {
        patient.studies.each { study ->
            final RadiologyReport radiologyReport = study.radReport
            final LocalDateTime reportDateTime = radiologyReport.reportDateTime
            final Path reportDayDirectory = hl7Directory
                .resolve(reportDateTime.year as String)
                .resolve(reportDateTime.monthValue as String)
                .resolve(reportDateTime.dayOfMonth as String)
            reportDayDirectory.toFile().mkdirs()

            new File(
                reportDayDirectory.toString(),
                "${radiologyReport.messageControlId}_${TimeUtils.HL7_FORMATTER_DATETIME.format(radiologyReport.reportDateTime)}.hl7"
            ).text = radiologyReport.produceReport(patient, study).encode()
        }
    }

}
