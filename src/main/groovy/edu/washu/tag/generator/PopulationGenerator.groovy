package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.patient.*
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator
import edu.washu.tag.util.FileIOUtils
import io.temporal.activity.Activity
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Consumer

import static java.lang.Math.round

class PopulationGenerator {

    SpecificationParameters specificationParameters = new YamlObjectMapper().readValue(FileIOUtils.readResource('basicRequest.yaml'), SpecificationParameters)
    boolean writeDataToFiles = false
    boolean generateTestQueries = false
    private final Logger logger = LoggerFactory.getLogger(PopulationGenerator)

    private static final EnumeratedDistribution<PatientRandomizer> patientRandomizers = RandomGenUtils.setupWeightedLottery([
            (new DefaultPatientRandomizer()) : 85,
            (new GreekPatientRandomizer()) : 5,
            (new JapanesePatientRandomizer()): 5,
            (new KoreanPatientRandomizer()): 5
    ])

    static void main(String[] args) {
        final PopulationGenerator generator = new PopulationGenerator()
        final String configName = args[0]
        if (configName != 'default') {
            generator.readSpecificationParameters(configName)
        }

        generator.setWriteDataToFiles(args[1] == 'true')
        generator.setGenerateTestQueries(args[2] == 'true')

        final NameCache nameCache = NameCache.initInstance()
        final IdOffsets idOffsets = new IdOffsets()

        final List<BatchRequest> batchRequests = generator.chunkRequest()[0].resolveToBatches()
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

        BatchProcessor.initDirs(args.length > 3 ? args[3] : '.')
        if (generator.writeDataToFiles) {
            new BatchProcessor(
                batches: batchSpecifications,
                generateTests: generator.generateTestQueries
            ).writeAndCombineBatches()
        }
    }

    List<BatchChunk> chunkRequest(int patientsPerFullBatch = BatchSpecification.MAX_PATIENTS, int chunks = 1) {
        new File('batches').mkdir() // while we're still in a single process

        final int totalNumPatients = specificationParameters.numPatients
        final int totalNumStudies = specificationParameters.numStudies
        final int totalNumSeries = specificationParameters.numSeries

        int minPatientsInChunk = totalNumPatients / chunks
        int patientRemainder = totalNumPatients % chunks
        int minStudiesInChunk = totalNumStudies / chunks
        int studyRemainder = totalNumStudies % chunks
        int minSeriesInChunk = totalNumSeries / chunks
        int seriesRemainder = totalNumSeries % chunks

        int batchOffset = 0
        int patientOffset = 0
        int studyOffset = 0

        (0 ..< chunks).collect { chunkIndex ->
            final BatchChunk batchChunk = new BatchChunk(
                numPatients: minPatientsInChunk + (chunkIndex < patientRemainder ? 1 : 0),
                numStudies: minStudiesInChunk + (chunkIndex < studyRemainder ? 1 : 0),
                numSeries: minSeriesInChunk + (chunkIndex < seriesRemainder ? 1 : 0),
                patientOffset: patientOffset,
                studyOffset: studyOffset,
                batchIdOffset: batchOffset,
                patientsPerFullBatch: patientsPerFullBatch
            )
            batchOffset += batchChunk.calculateNumBatches()
            patientOffset += batchChunk.numPatients
            studyOffset += batchChunk.numStudies

            batchChunk
        }
    }

    void readSpecificationParameters(String configName) {
        setSpecificationParameters(
            new YamlObjectMapper().readValue(new File(configName), SpecificationParameters)
        )
    }

    BatchSpecification generateBatch(NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest) {
        final BatchSpecification batchSpecification = new BatchSpecification(id: batchRequest.id)
        final Consumer<Patient> patientConsumer = { Patient patient -> batchSpecification.patients << patient }
        generateBatchWithHandler(nameCache, idOffsets, batchRequest, patientConsumer, false)

        if (specificationParameters.generateRadiologyReports) {
            specificationParameters.reportGeneratorImplementation.generateReportsForPatients(batchSpecification.patients, false)
        }

        batchSpecification.writeToFile()
        batchSpecification
    }

    void generateAndWriteFullBatch(NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest, boolean writeDicom, boolean writeHl7, boolean temporalHeartbeat = false) {
        final OutputHandler outputHandler = new OutputHandler(batchRequest.id)
        final Consumer<Patient> patientConsumer = { Patient patient ->
            if (writeDicom) {
                outputHandler.writeDicomForPatient(patient)
            }
            if (writeHl7) {
                outputHandler.writeHl7ForPatient(patient)
            }
        }
        generateBatchWithHandler(nameCache, idOffsets, batchRequest, patientConsumer, true, temporalHeartbeat)
    }

    private void generateBatchWithHandler(NameCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest, Consumer<Patient> handler, boolean generateReports, boolean temporalHeartbeat = false) {
        specificationParameters.postprocess()
        NameCache.cache(nameCache)

        final GenerationContext generationContext = new GenerationContext(
            specificationParameters: specificationParameters,
            patientIdEncoders: idOffsets.getPatientIdEncodersFromOffset(batchRequest.patientOffset),
            studyCountOverride: 0
        )
        int generatedStudies = 0
        int generatedSeries = 0
        final SequentialIdGenerator studyIdGenerator = idOffsets.getStudyIdEncoderFromOffset(batchRequest.studyOffset)
        final List<Patient> patients = []

        batchRequest.numPatients.times { generatedPatients ->
            generationContext.setCurrentAverageStudiesPerPatient(generatedPatients == 0 ? 0.0 : generatedStudies / generatedPatients)
            generationContext.setPreviouslyGeneratedSeries(generatedSeries)
            generationContext.setPreviouslyGeneratedStudies(generatedStudies)
            if (generatedPatients == batchRequest.numPatients - 1) { // for last patient in batch, we need to ensure exact number of studies
                generationContext.setStudyCountOverride(batchRequest.numStudies - generatedStudies)
            }
            final Patient patient = patientRandomizers.sample().createPatient(specificationParameters)
            patient.randomize(generationContext, studyIdGenerator)
            patient.studies.each { study ->
                generatedStudies++
                generatedSeries += study.series.size()
            }
            if (temporalHeartbeat) {
                Activity.executionContext.heartbeat("Batch ${batchRequest.id}, patient ${generatedPatients + 1} post-DICOM")
            }
            patients << patient
        }

        logger.info("DICOM specs for batch ${batchRequest.id} with ${patients.size()} patients and ${patients*.studies.sum()} studies")

        if (specificationParameters.generateRadiologyReports && generateReports) {
            specificationParameters.reportGeneratorImplementation.generateReportsForPatients(patients, temporalHeartbeat)
        }

        patients.eachWithIndex { patient, generatedPatients ->
            handler.accept(patient)
            if ((generatedPatients + 1) % 100 == 0) {
                logger.info("Generated DICOM and/or HL7 for patient ${patient.patientInstanceUid} in batch ${batchRequest.id} [${generatedPatients + 1}/${batchRequest.numPatients}]")
            }
            if (temporalHeartbeat) {
                Activity.executionContext.heartbeat("Batch ${batchRequest.id}, patient ${generatedPatients + 1}")
            }
        }
    }

}
