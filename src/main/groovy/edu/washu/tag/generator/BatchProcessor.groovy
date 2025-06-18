package edu.washu.tag.generator

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

    static void main(String[] args) {
        initDirs(args.length > 3 ? args[3] : '.')
        final BatchProcessor batchProcessor = new BatchProcessor(batches: args[0].split(',').collect {
            new File(it)
        })
        if (args.length > 1) {
            batchProcessor.setWriteFiles(Boolean.parseBoolean(args[1]))
        }
        if (args.length > 2) {
            batchProcessor.setGenerateTests(Boolean.parseBoolean(args[2]))
        }

        batchProcessor.writeAndCombineBatches()
    }

    static void initDirs(String outputDirectory) {
        final Path output = Paths.get(outputDirectory)
        output.toFile().mkdir()
        hl7Output = output.resolve('hl7').toFile()
        dicomOutput = output.resolve('dicom_output').toFile()
        [dicomOutput, hl7Output].each { outputDir ->
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

    void writeAndCombineBatches() {
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
