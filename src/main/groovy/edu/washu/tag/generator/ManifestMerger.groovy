package edu.washu.tag.generator

import com.fasterxml.jackson.databind.ObjectMapper

class ManifestMerger {

    /**
     * Reads every batch subdirectory's manifest.json under hl7BaseDir, unions them (list values
     * at matching leaf keys are concatenated, never overwritten), and writes the combined result
     * to manifest.json alongside hl7BaseDir. A no-op - writes nothing - if no batch wrote one.
     */
    static void combineManifests(File hl7BaseDir) {
        final ObjectMapper objectMapper = new ObjectMapper()
        final List<Map<String, Object>> perBatchManifests = (hl7BaseDir.listFiles(File::isDirectory as FileFilter) as List<File>)
            .collect { new File(it, 'manifest.json') }
            .findAll { it.exists() }
            .collect { objectMapper.readValue(it, Map) as Map<String, Object> }

        if (!perBatchManifests) {
            return
        }

        final Map<String, Object> combined = [:]
        perBatchManifests.each { mergeInto(combined, it) }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(hl7BaseDir.parentFile, 'manifest.json'), combined)
    }

    private static void mergeInto(Map<String, Object> target, Map<String, Object> source) {
        source.each { key, value ->
            if (value instanceof Map) {
                mergeInto(target.computeIfAbsent(key) { [:] } as Map<String, Object>, value as Map<String, Object>)
            } else if (value instanceof List) {
                (target.computeIfAbsent(key) { [] } as List).addAll(value as List)
            } else {
                target[key] = value
            }
        }
    }

}
