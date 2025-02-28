package edu.washu.tag.generator

import edu.washu.tag.generator.query.QueryGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BatchProcessor {

    List<File> batches
    boolean writeFiles = true // DICOM & HL7 [if generated]
    boolean generateTests = false
    boolean suppressDicom = false
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor)

    static void main(String[] args) {
        final List<String> batches = args[0].split(',')
        logger.info("Attempting to process batches: ${batches}")
        final BatchProcessor batchProcessor = new BatchProcessor(batches: batches.collect {
            new File(it)
        })
        if (args.length > 1) {
            batchProcessor.setWriteFiles(Boolean.parseBoolean(args[1]))
            logger.info("writeFiles set to ${batchProcessor.writeFiles}")
        }
        if (args.length > 2) {
            batchProcessor.setGenerateTests(Boolean.parseBoolean(args[2]))
            logger.info("generateTests set to ${batchProcessor.generateTests}")
        }
        if (args.length > 3) {
            batchProcessor.setSuppressDicom(Boolean.parseBoolean(args[3]))
            logger.info("suppressDicom set to ${batchProcessor.suppressDicom}")
        }

        batchProcessor.writeBatches()
    }

    void writeBatches() {
        final YamlObjectMapper objectMapper = new YamlObjectMapper()
        final File dicomOutput = new File('dicom_output')
        final File hl7Output = new File('hl7')
        [dicomOutput, hl7Output].each { outputDir ->
            if (!outputDir.exists()) {
                outputDir.mkdir()
            }
        }

        println("STAGE 2: ${batches.size()} batch file${batches.size() > 1 ? 's' : ''} will be written to files and/or used to create test queries.")
        boolean radReportWritten = false
        final QueryGenerator queryGenerator = new QueryGenerator()

        batches.eachWithIndex { batchFile, index ->
            final BatchSpecification batch = objectMapper.readValue(batchFile, BatchSpecification)
            logger.info("Read batch from file into memory.")
            if (writeFiles) {
                if (!suppressDicom) {
                    batch.generateDicom(index, batches.size(), dicomOutput)
                }
                if (batch.containsRadiologyReport()) {
                    batch.generateHl7(index, batches.size(), hl7Output)
                    radReportWritten = true
                }
            }
            if (generateTests) {
                queryGenerator.processData(batch)
            }
        }

        if (radReportWritten) {
            new Hl7Logger().writeToHl7ishLogFiles(hl7Output)
        }

        if (generateTests) {
            queryGenerator.writeQueries()
        }
    }

}
