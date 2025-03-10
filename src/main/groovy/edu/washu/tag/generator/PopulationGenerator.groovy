package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.patient.*
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator
import edu.washu.tag.util.FileIOUtils
import groovyx.gpars.GParsPool
import org.apache.commons.math3.distribution.EnumeratedDistribution

import static java.lang.Math.round

class PopulationGenerator {

    SpecificationParameters specificationParameters = new YamlObjectMapper().readValue(FileIOUtils.readResource('basicRequest.yaml'), SpecificationParameters)
    boolean writeDataToFiles = false
    boolean generateTestQueries = false

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
        generator.setGenerateTestQueries(args[2] == 'true')

        final NameCache nameCache = NameCache.initInstance()
        final IdOffsets idOffsets = new IdOffsets()

        final List<BatchRequest> batchRequests = generator.chunkRequest()
        final String batchFulfillment = batchRequests.size() > 1 ? "split into ${batchRequests.size()} batches" : 'fulfilled in a single batch'
        println("STAGE 1: request will be ${batchFulfillment}")

        final List<File> batchSpecifications = batchRequests.collect { batchRequest ->
            final File batchFile = generator
                .generateBatch(nameCache, idOffsets, batchRequest)
                .asFile()
            println("Specification for batch #${batchRequest.id} (out of ${batchRequests.size()}) has been created")
            batchFile
        }
        println("STAGE 1 complete, batches have been generated")

        BatchProcessor.initDirs()
        if (generator.writeDataToFiles) {
            new BatchProcessor(
                batches: batchSpecifications,
                generateTests: generator.generateTestQueries
            ).writeAndCombineBatches()
        }
    }

    List<BatchRequest> chunkRequest() {
        new File('batches').mkdir() // while we're still in a single process
        final int totalNumPatients = specificationParameters.numPatients
        final int totalNumStudies = specificationParameters.numStudies
        final int totalNumSeries = specificationParameters.numSeries
        final int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS
        final int patientsInIncompleteBatch = totalNumPatients % patientsPerFullBatch
        final boolean hasIncompleteBatch = patientsInIncompleteBatch != 0
        final int studiesPerFullBatch = round((patientsPerFullBatch * totalNumStudies) / totalNumPatients).intValue()
        final int studiesInIncompleteBatch = totalNumStudies % studiesPerFullBatch
        final int seriesPerFullBatch = round((patientsPerFullBatch * totalNumSeries) / totalNumPatients).intValue()
        final int seriesInIncompleteBatch = totalNumSeries % seriesPerFullBatch
        final int totalNumBatches = Math.ceil(specificationParameters.numPatients / BatchSpecification.MAX_PATIENTS).intValue()
        (0 ..< totalNumBatches).collect { batchId ->
            final boolean isPartialBatch = hasIncompleteBatch && batchId == totalNumBatches - 1
            new BatchRequest(
                id: batchId,
                numPatients: isPartialBatch ? patientsInIncompleteBatch : patientsPerFullBatch,
                numStudies: isPartialBatch ? studiesInIncompleteBatch : studiesPerFullBatch,
                numSeries:  isPartialBatch ? seriesInIncompleteBatch : seriesPerFullBatch,
                patientOffset: patientsPerFullBatch * batchId,
                studyOffset: studiesPerFullBatch * batchId
            )
        }
    }

    BatchSpecification generateBatch(NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest) {
        specificationParameters.postprocess()
        NameCache.cache(nameCache)

        final BatchSpecification batchSpecification = new BatchSpecification(id: batchRequest.id)
        final GenerationContext generationContext = new GenerationContext(
            specificationParameters: specificationParameters,
            patientIdEncoders: idOffsets.getPatientIdEncodersFromOffset(batchRequest.patientOffset),
            studyCountOverride: 0
        )
        int generatedStudies = 0
        int generatedSeries = 0
        final SequentialIdGenerator studyIdGenerator = idOffsets.getStudyIdEncoderFromOffset(batchRequest.studyOffset)

        batchRequest.numPatients.times { generatedPatients ->
            generationContext.setCurrentAverageStudiesPerPatient(generatedPatients == 0 ? 0.0 : generatedStudies / generatedPatients)
            generationContext.setPreviouslyGeneratedSeries(generatedSeries)
            generationContext.setPreviouslyGeneratedStudies(generatedStudies)
            if (generatedPatients == batchRequest.numPatients - 1) { // for last patient in batch, we need to ensure exact number of studies
                generationContext.setStudyCountOverride(batchRequest.numStudies - generatedStudies)
            }
            final Patient patient = patientRandomizers.sample().createPatient(specificationParameters)
            patient.randomize(generationContext, studyIdGenerator)
            batchSpecification.patients << patient
            patient.studies.each { study ->
                generatedStudies++
                generatedSeries += study.series.size()
            }
        }

        if (specificationParameters.generateRadiologyReports) {
            GParsPool.withPool {
                batchSpecification.patients.eachParallel { Patient patient ->
                    specificationParameters.reportGeneratorImplementation.generateReportsForPatient(patient)
                }
            }
        }

        batchSpecification.writeToFile()
        batchSpecification
    }

}
