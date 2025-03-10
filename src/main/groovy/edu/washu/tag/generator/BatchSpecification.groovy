package edu.washu.tag.generator

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.QuerySourceData
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

import java.nio.file.Path
import java.time.LocalDateTime

class BatchSpecification implements QuerySourceData {

    int id
    List<Patient> patients = []
    @JsonIgnore written = false
    public static final int MAX_PATIENTS = 2000
    public static final int MAX_PATIENTS_PER_LAYER = 100

    @JsonIgnore
    boolean isFull() {
        patients.size() > MAX_PATIENTS // TODO: what if a few patients have many, many, many studies?
    }

    String relativeFilePath() {
        "batches/batch_${id}.yaml"
    }

    File asFile() {
        new File(relativeFilePath())
    }

    void writeToFile() {
        new YamlObjectMapper().writeValue(asFile(), this)
        written = true
    }

    void generateDicom(int index, int totalNumBatches, File outputDir) {
        println("Writing batch with id ${id} to DICOM...")
        final Path batchDirectory = outputDir.toPath().resolve("batch_${id}")
        batchDirectory.toFile().mkdir()
        int layerId = -1
        int fileId
        int patientsWritten = 0
        Path layer

        patients.each { patient ->
            if (patientsWritten % MAX_PATIENTS_PER_LAYER == 0) {
                layerId++
                fileId = 1
                layer = batchDirectory.resolve("layer_${layerId}")
                layer.toFile().mkdir()
            }

            patient.studies.each { study ->
                if (patient.race != null) {
                    study.setEthnicGroup(patient.race.sampleEncodedValue())
                }
                study.resolvePrivateElements(patient, [])
                study.series.each { series ->
                    series.instances.each { instance ->
                        instance.writeToDicomFile(patient, study, series, layer.resolve("${fileId}.dcm").toFile())
                        fileId++
                    }
                }
            }
            patientsWritten++
        }
        println("Successfully wrote batch with id ${id} to DICOM [${index + 1}/${totalNumBatches}]")
    }

    void generateHl7(int index, int totalNumBatches, File outputDir) {
        println("Writing batch with id ${id} to HL7 v2...")

        patients.each { patient ->
            patient.studies.each { study ->
                final RadiologyReport radiologyReport = study.radReport
                final LocalDateTime reportDateTime = radiologyReport.reportDateTime
                final Path reportDayDirectory = outputDir
                        .toPath()
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

        println("Successfully wrote batch with id ${id} to HL7 [${index + 1}/${totalNumBatches}]")
    }

    boolean containsRadiologyReport() {
        patients.any { patient ->
            patient.studies.any { study ->
                study.radReport != null
            }
        }
    }

}
