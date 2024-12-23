package edu.washu.tag.generator

class BatchProcessor {

    List<File> batches

    static void main(String[] args) {
        new BatchProcessor(batches: args[0].split(',').collect {
            new File(it)
        }).writeBatches()
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

        println("STAGE 2: ${batches.size()} batch file${batches.size() > 1 ? 's' : ''} will be written to files.")
        boolean radReportWritten = false

        batches.eachWithIndex { batchFile, index ->
            final BatchSpecification batch = objectMapper.readValue(batchFile, BatchSpecification)
            batch.generateDicom(index, batches.size(), dicomOutput)
            if (batch.containsRadiologyReport()) {
                batch.generateHl7(index, batches.size(), hl7Output)
                radReportWritten = true
            }

        }

        if (radReportWritten) {
            new Hl7Logger().writeToHl7ishLogFiles(hl7Output)
        }
    }

}
