package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.metadata.Study
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
    private boolean dicomDirPrepared = false
    private boolean hl7DirPrepared = false
    // cohort name -> step name -> chosen additionalGenerationContextOptions entry -> message control ids
    private final Map<String, Map<String, Map<String, List<String>>>> manifest = [:]

    OutputHandler(int id) {
        batchDirectory = BatchProcessor.dicomOutput.toPath().resolve("batch_${id}")
        hl7Directory = BatchProcessor.hl7Output.toPath().resolve("batch_${id}")
    }

    void writeDicomForPatient(Patient patient) {
        prepareDicomDirectory()
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
        prepareHl7Directory()
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
            recordManifestEntry(patient, study, radiologyReport)
        }
    }

    /**
     * Records which additionalGenerationContextOptions entry (if any) produced this study's report, keyed by
     * the resolved cohort/step it came from. Only cohort patients whose step actually used weighted options
     * produce an entry - nothing to disambiguate otherwise.
     */
    private void recordManifestEntry(Patient patient, Study study, RadiologyReport radiologyReport) {
        final String cohortName = patient.parentCohort?.name
        final String chosenOption = study.chosenGenerationContextOption
        if (!cohortName || !chosenOption) {
            return
        }
        manifest
            .computeIfAbsent(cohortName) { [:] }
            .computeIfAbsent(study.cohortStepName) { [:] }
            .computeIfAbsent(chosenOption) { [] }
            << radiologyReport.messageControlId
    }

    /**
     * Writes this batch's manifest to manifest.json alongside its HL7 output. Call once, after every patient
     * in the batch has been written. A no-op if nothing in this batch used weighted options.
     */
    void writeManifest() {
        if (!hl7DirPrepared || manifest.isEmpty()) {
            return
        }
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(hl7Directory.toFile(), 'manifest.json'), manifest)
    }

    /**
     * Wipes this batch's DICOM output directory before the first write. Batch activities are executed
     * at-least-once by Temporal, so a retry after a partial failure would otherwise layer fresh output
     * (with new, randomly generated UIDs) on top of orphaned files from the failed attempt. Clearing the
     * batch-scoped directory first guarantees the retry produces exactly the batch's output and nothing else.
     */
    private void prepareDicomDirectory() {
        if (!dicomDirPrepared) {
            batchDirectory.toFile().deleteDir()
            batchDirectory.toFile().mkdirs()
            dicomDirPrepared = true
        }
    }

    /**
     * Wipes this batch's HL7 output subtree before the first write, for the same retry-idempotency reason as
     * {@link #prepareDicomDirectory()}. HL7 is written under a per-batch subtree (hl7/batch_&lt;id&gt;/) precisely
     * so a single batch can be cleared and rewritten without disturbing other batches' files; the combine step
     * later collapses these subtrees back into one log per calendar day.
     */
    private void prepareHl7Directory() {
        if (!hl7DirPrepared) {
            hl7Directory.toFile().deleteDir()
            hl7Directory.toFile().mkdirs()
            hl7DirPrepared = true
        }
    }

}
