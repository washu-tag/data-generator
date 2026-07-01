package edu.washu.tag.generator

import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.patient.*
import edu.washu.tag.generator.query.QueryUtils
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.util.FileIOUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.function.Consumer

class PopulationGenerator {

    SpecificationParameters specificationParameters = new YamlObjectMapper().readValue(FileIOUtils.readResource('basicRequest.yaml'), SpecificationParameters)
    EnumeratedDistribution<PatientRandomizer> patientRandomizers = initPatientRandomizers()
    boolean writeDataToFiles = false
    boolean generateTestQueries = false

    static void main(String[] args) {
        final PopulationGenerator generator = new PopulationGenerator()
        final String configName = args[0]
        if (configName != 'default') {
            generator.readSpecificationParameters(configName)
        }

        generator.setWriteDataToFiles(args[1] == 'true')
        generator.setGenerateTestQueries(args[2] == 'true')

        final GenerationCache generationCache = GenerationCache.initInstance(generator.specificationParameters)
        final IdOffsets idOffsets = new IdOffsets()
        CodeCache.initializeCache()

        final List<BatchRequest> batchRequests = new Batcher(generator.specificationParameters).resolveBatches()
        final String batchFulfillment = batchRequests.size() > 1 ? "split into ${batchRequests.size()} batches" : 'fulfilled in a single batch'
        println("STAGE 1: request will be ${batchFulfillment}")

        final List<File> batchSpecifications = batchRequests.collect { batchRequest ->
            final File batchFile = generator
                .generateBatch(generationCache, idOffsets, batchRequest)
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
            ).writeAndCombineBatches(generationCache)
        }
    }

    void readSpecificationParameters(String configName) {
        initFromSpecificationParameters(
            new YamlObjectMapper().readValue(new File(configName), SpecificationParameters)
        )
    }

    void initFromSpecificationParameters(SpecificationParameters specificationParameters) {
        this.specificationParameters = specificationParameters
        patientRandomizers = initPatientRandomizers()
    }

    BatchSpecification generateBatch(GenerationCache nameCache, IdOffsets idOffsets, BatchRequest batchRequest) {
        final BatchSpecification batchSpecification = new BatchSpecification(id: batchRequest.id)
        final Consumer<Patient> patientConsumer = { Patient patient -> batchSpecification.patients << patient }
        generateBatchWithHandler(nameCache, idOffsets, batchRequest, patientConsumer, false)

        if (specificationParameters.generateRadiologyReports) {
            specificationParameters.reportGeneratorImplementation.generateReportsForPatients(batchSpecification.patients, false, specificationParameters.customReportGuarantees)
        }

        if (generateTestQueries) {
            final Closure<RadiologyReport> firstReportWithText = {
                for (Patient patient : batchSpecification.patients) {
                    for (Study study : patient.studies) {
                        final RadiologyReport radiologyReport = study.radReport
                        if (radiologyReport.includeObx && radiologyReport.generatedReport instanceof ClassicReport) {
                            return radiologyReport
                        }
                    }
                }
                throw new RuntimeException('Should not happen')
            }
            final RadiologyReport radiologyReport = firstReportWithText.call()
            final ClassicReport generatedReport = radiologyReport.generatedReport as ClassicReport
            generatedReport.setFindings(QueryUtils.SPECIAL_CHAR_INSERT + generatedReport.findings)
        }

        batchSpecification.writeToFile()
        batchSpecification
    }

    void generateAndWriteFullBatch(GenerationCache generationCache, IdOffsets idOffsets, BatchRequest batchRequest, boolean writeDicom, boolean writeHl7, boolean temporalHeartbeat = false) {
        final OutputHandler outputHandler = new OutputHandler(batchRequest.id)
        final Consumer<Patient> patientConsumer = { Patient patient ->
            if (writeDicom) {
                outputHandler.writeDicomForPatient(patient)
            }
            if (writeHl7) {
                outputHandler.writeHl7ForPatient(patient)
            }
        }
        generateBatchWithHandler(generationCache, idOffsets, batchRequest, patientConsumer, true, temporalHeartbeat)
    }

    private void generateBatchWithHandler(GenerationCache generationCache, IdOffsets idOffsets, BatchRequest batchRequest, Consumer<Patient> handler, boolean generateReports, boolean temporalHeartbeat = false) {
        batchRequest.generateBatchWithHandler(
            new BatchRequestContext()
                .specificationParameters(specificationParameters)
                .generationCache(generationCache)
                .idOffsets(idOffsets)
                .patientRandomizers(patientRandomizers)
                .handler(handler)
                .generateReports(generateReports)
                .temporalHeartbeat(temporalHeartbeat)
        )
    }

    private EnumeratedDistribution<PatientRandomizer> initPatientRandomizers() {
        RandomGenUtils.setupWeightedLottery(
            specificationParameters.patientRandomizers.collectEntries { randomizer ->
                [(randomizer): randomizer.randomizerWeight]
            }
        )
    }

}
