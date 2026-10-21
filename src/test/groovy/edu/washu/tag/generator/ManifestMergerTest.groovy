package edu.washu.tag.generator

import com.fasterxml.jackson.databind.ObjectMapper
import org.testng.annotations.Test

import java.nio.file.Files

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse

class ManifestMergerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()

    @Test
    void testUnionsOverlappingKeysAcrossBatches() {
        final File hl7BaseDir = hl7DirUnderFreshDatasetRoot()
        writeBatchManifest(hl7BaseDir, 'batch_0', [
            cohortA: [stepX: [optionOne: ['2.25.111', '2.25.222']]]
        ])
        writeBatchManifest(hl7BaseDir, 'batch_1', [
            cohortA: [stepX: [optionOne: ['2.25.333']]]
        ])

        ManifestMerger.combineManifests(hl7BaseDir)

        final Map combined = MAPPER.readValue(new File(hl7BaseDir.parentFile, 'manifest.json'), Map)
        assertEquals(combined.cohortA.stepX.optionOne, ['2.25.111', '2.25.222', '2.25.333'])
    }

    @Test
    void testKeepsDisjointCohortsStepsAndOptionsSeparate() {
        final File hl7BaseDir = hl7DirUnderFreshDatasetRoot()
        writeBatchManifest(hl7BaseDir, 'batch_0', [
            'lr_tiv_and_lr_m/lr_tiv': [diagnostic_ct: [optionA: ['2.25.111']]]
        ])
        writeBatchManifest(hl7BaseDir, 'batch_1', [
            'lr_tiv_and_lr_m/lr_m': [diagnostic_ct: [optionA: ['2.25.222']]]
        ])

        ManifestMerger.combineManifests(hl7BaseDir)

        final Map combined = MAPPER.readValue(new File(hl7BaseDir.parentFile, 'manifest.json'), Map)
        assertEquals(combined['lr_tiv_and_lr_m/lr_tiv'].diagnostic_ct.optionA, ['2.25.111'])
        assertEquals(combined['lr_tiv_and_lr_m/lr_m'].diagnostic_ct.optionA, ['2.25.222'])
    }

    @Test
    void testNoBatchManifestsProducesNoCombinedFile() {
        final File hl7BaseDir = hl7DirUnderFreshDatasetRoot()
        new File(hl7BaseDir, 'batch_0').mkdirs() // batch ran, but nothing used weighted options

        ManifestMerger.combineManifests(hl7BaseDir)

        assertFalse(new File(hl7BaseDir.parentFile, 'manifest.json').exists())
    }

    // Each test gets its own dataset root (not a shared system temp dir) as hl7BaseDir's parent,
    // so combined manifest.json files from different tests can never collide.
    private static File hl7DirUnderFreshDatasetRoot() {
        final File datasetRoot = Files.createTempDirectory('manifest-merger-test').toFile()
        new File(datasetRoot, 'hl7')
    }

    private static void writeBatchManifest(File hl7BaseDir, String batchName, Map<String, Object> contents) {
        final File batchDir = new File(hl7BaseDir, batchName)
        batchDir.mkdirs()
        MAPPER.writeValue(new File(batchDir, 'manifest.json'), contents)
    }

}
