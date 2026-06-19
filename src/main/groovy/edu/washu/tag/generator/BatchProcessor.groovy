package edu.washu.tag.generator

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.query.QueryGenerator

import java.nio.file.Path
import java.nio.file.Paths

class BatchProcessor {

    List<File> batches
    boolean writeFiles = true // DICOM & HL7 [if generated]
    boolean generateTests = false
    boolean radReportWritten = false
    static File hl7Output
    static File dicomOutput
    static File logOutput

    static void main(String[] args) {
        initDirs(args.length > 4 ? args[4] : '.')
        final BatchProcessor batchProcessor = new BatchProcessor(batches: args[0].split(',').collect {
            new File(it)
        })
        final GenerationCache generationCache = new ObjectMapper().readValue(args[1], GenerationCache)
        if (args.length > 2) {
            batchProcessor.setWriteFiles(Boolean.parseBoolean(args[2]))
        }
        if (args.length > 3) {
            batchProcessor.setGenerateTests(Boolean.parseBoolean(args[3]))
        }

        batchProcessor.writeAndCombineBatches(generationCache)
    }

    static void initDirs(String outputDirectory) {
        final Path output = Paths.get(outputDirectory)
        hl7Output = output.resolve('hl7').toFile()
        dicomOutput = output.resolve('dicom_output').toFile()
        logOutput = output.resolve('hl7ish_logs').toFile()
        [output.toFile(), dicomOutput, hl7Output, logOutput].each { outputDir ->
            if (!outputDir.exists()) {
                outputDir.mkdir()
            }
        }
    }

    void writeBatches() {
        final YamlObjectMapper objectMapper = new YamlObjectMapper()

        println("STAGE 2: ${batches.size()} batch file${batches.size() > 1 ? 's' : ''} will be written to files and/or used to create test queries.")
        final QueryGenerator queryGenerator = new QueryGenerator()

        batches.eachWithIndex { batchFile, index ->
            final BatchSpecification batch = objectMapper.readValue(batchFile, BatchSpecification)
            writeSpec(batch, index)
            if (generateTests) {
                queryGenerator.processData(batch)
            }
        }

        if (generateTests) {
            queryGenerator.writeQueries()
        }
        println("STAGE 2 complete: (DICOM/HL7/test queries)")
    }

    void writeSpec(BatchSpecification batch, int index, Integer numBatches = batches.size()) {
        if (writeFiles) {
            batch.generateDicom(index, numBatches)
            if (batch.containsRadiologyReport()) {
                batch.generateHl7(index, numBatches)
                radReportWritten = true
            }
        }
    }

    void writeAndCombineBatches(GenerationCache generationCache) {
        generationCache.cache()
        writeBatches()

        if (radReportWritten) {
            println("STAGE 3: starting to combine separate HL7 files into pseudo-HL7 log format")
            final Hl7Logger hl7Logger = new Hl7Logger()
            hl7Logger.identifyHl7LogFiles(hl7Output).each { hl7LogFile ->
                hl7Logger.writeToHl7ishLogFile(hl7LogFile)
                println("Wrote log file: ${hl7LogFile.asFile.name}")
            }
            println("STAGE 3 complete (HL7-ish logs)")
        }
    }

}
