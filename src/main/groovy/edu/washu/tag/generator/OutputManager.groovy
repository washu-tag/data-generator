package edu.washu.tag.generator

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.metadata.GenerationCache
import io.temporal.failure.ApplicationFailure

import java.nio.file.Files
import java.nio.file.Paths

class OutputManager {

    String outputDir
    private ObjectMapper objectMapper
    private YamlObjectMapper yamlObjectMapper
    private static final String SPECIFICATION_FILE_NAME = 'specification_parameters.yaml'
    private static final String GENERATION_CACHE_FILE_NAME = 'generation_cache.json'
    private static final String ID_OFFSETS_FILE_NAME = 'fixed_id_offsets.json'
    private static final FileFilter IS_CONTINUATION = { file -> file.name.contains('continuation_') }

    OutputManager(String outputDir) {
        this.outputDir = outputDir
        objectMapper = new ObjectMapper()
        yamlObjectMapper = new YamlObjectMapper()
    }

    void ensureNonempty() {
        final File asFile = new File(outputDir)
        if (asFile.exists() && asFile.listFiles().length > 0) {
            throw ApplicationFailure.newNonRetryableFailure("Specified output directory already exists and is not empty", 'dangerous-request')
        }
    }

    SpecificationParameters readSpecificationParametersFromOutputDir() {
        read(SPECIFICATION_FILE_NAME, SpecificationParameters)
    }

    void copySpecificationParameters(String source) {
        Files.copy(
            Paths.get(source),
            Paths.get(outputDir, SPECIFICATION_FILE_NAME)
        )
    }

    String getContinuationSpecificationParametersPath(Continuation continuation) {
        Paths.get(outputDir, continuation.specificationParamsFileName()).toString()
    }

    void writeContinuationSpecificationParameters(SpecificationParameters specificationParameters, Continuation continuation) {
        write(continuation.specificationParamsFileName(), specificationParameters)
    }

    Continuation findLatestContinuation() {
        final File latest = new File(outputDir).listFiles(IS_CONTINUATION).max { file ->
            (file.name =~ /\d+/)[0] as int
        }
        read(latest.name, Continuation)
    }

    void writeContinuationToOutput(Continuation continuation) {
        write("continuation_${continuation.continuationId}.json", continuation)
    }

    Batcher initBatcherFromLatestContinuation(SpecificationParameters newSpec, int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS) {
        final Continuation continuation = findLatestContinuation()
        final Batcher batcher = new Batcher(newSpec, patientsPerFullBatch)
        batcher.setPatientOffset(continuation.patientOffset)
        batcher.setStudyOffset(continuation.studyOffset)
        batcher.setIdOffset(continuation.batchIdOffset)
        batcher.setContinuationId(continuation.continuationId + 1)
        batcher
    }

    GenerationCache readGenerationCache() {
        read(GENERATION_CACHE_FILE_NAME, GenerationCache)
    }

    void writeGenerationCacheToFile(GenerationCache generationCache) {
        write(GENERATION_CACHE_FILE_NAME, generationCache)
    }

    IdOffsets readFixedOffsets() {
        read(ID_OFFSETS_FILE_NAME, IdOffsets)
    }

    void writeFixedOffsets(IdOffsets idOffsets) {
        write(ID_OFFSETS_FILE_NAME, idOffsets)
    }

    static String prefixOutput(String path) {
        if (path.startsWith('/output')) {
            path
        } else {
            Paths.get('/output', path).toString()
        }
    }

    private <X> X read(String file, Class<X> objClass) {
        mapperFor(file).readValue(
            Paths.get(outputDir, file).toFile(),
            objClass
        )
    }

    private <X> void write(String file, X obj) {
        mapperFor(file).writeValue(
            Paths.get(outputDir, file).toFile(),
            obj
        )
    }

    private ObjectMapper mapperFor(String file) {
        file.endsWith('yaml') ? yamlObjectMapper : objectMapper
    }

}
