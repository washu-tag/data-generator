package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.patient.*
import edu.washu.tag.generator.util.FileIOUtils
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.concurrent.ThreadLocalRandom

class PopulationGenerator {

    SpecificationParameters specificationParameters = new YamlObjectMapper().readValue(FileIOUtils.readResource('basicRequest.yaml'), SpecificationParameters)
    boolean writeDataToFiles = false
    private static final EnumeratedDistribution<PatientRandomizer> patientRandomizers = RandomGenUtils.setupWeightedLottery([
            (new DefaultPatientRandomizer()) : 85,
            (new GreekPatientRandomizer()) : 5,
            (new JapanesePatientRandomizer()): 5,
            (new KoreanPatientRandomizer()): 5
    ])

    static void main(String[] args) {
        final PopulationGenerator generator = new PopulationGenerator()
        final String configName = args[0]
        generator.setSpecificationParameters(
                new YamlObjectMapper().readValue(
                        configName == 'default' ?
                                FileIOUtils.readResource('basicRequest.yaml') :
                                new File(configName).text,
                        SpecificationParameters
                )
        )
        generator.setWriteDataToFiles(args[1] == 'true')
        generator.generate()
    }

    void generate() {
        long generatedPatients = 0
        long generatedStudies = 0
        long generatedSeries = 0
        long nextAccessionNumber = 2000000000 + ThreadLocalRandom.current().nextLong(7000000000)

        specificationParameters.postprocess()
        BatchSpecification currentBatch = new BatchSpecification(id : 0)

        new File('batches').mkdir()
        final int expectedNumBatches = Math.ceil(specificationParameters.numPatients / BatchSpecification.MAX_PATIENTS).intValue()

        println("STAGE 1: you requested data for ${specificationParameters.numPatients} patients. Manifests for creating the DICOM will first be generated in about ${expectedNumBatches} batch${expectedNumBatches > 1 ? 'es' : ''}")

        while (generatedPatients < specificationParameters.numPatients || generatedStudies < specificationParameters.numStudies || generatedSeries < specificationParameters.numSeries) {
            final int currentAverageStudiesPerPatient = generatedPatients == 0 ? 0 : generatedStudies / generatedPatients
            final Patient patient = patientRandomizers.sample().createPatient(specificationParameters)
            patient.randomize(specificationParameters, currentAverageStudiesPerPatient, generatedSeries, generatedStudies)
            currentBatch.patients << patient
            generatedPatients++
            patient.studies.each { study ->
                study.setAccessionNumber(nextAccessionNumber.toString())
                study.setStudyId(study.accessionNumber)
                nextAccessionNumber++
                generatedStudies++
                generatedSeries += study.series.size()
            }
            if (currentBatch.isFull()) {
                currentBatch.writeToFileAndLog(expectedNumBatches, specificationParameters)
                currentBatch = new BatchSpecification(id : currentBatch.id + 1)
            }
        }

        if (!currentBatch.written && currentBatch.patients.size() > 0) {
            currentBatch.writeToFileAndLog(expectedNumBatches, specificationParameters)
        }

        println("STAGE 1 COMPLETE: manifests for data of ${specificationParameters.numPatients} patients have been created.")

        if (writeDataToFiles) {
            new BatchProcessor(batches:
                    (0 .. currentBatch.id).collect { id ->
                        new File(new BatchSpecification(id: id).relativeFilePath())
                    }
            ).writeBatches()
        }
    }

}
