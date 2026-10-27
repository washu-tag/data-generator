package edu.washu.tag.generator

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.ai.catalog.LiRadsReport
import edu.washu.tag.generator.ai.catalog.ReportRegistry
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import edu.washu.tag.generator.hl7.v2.CyclicVariedGenerator
import edu.washu.tag.generator.metadata.Diagnosis
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.metadata.cohorting.StudyRequest
import edu.washu.tag.generator.metadata.patient.DefaultPatientRandomizer
import edu.washu.tag.generator.metadata.patient.PatientRandomizer
import edu.washu.tag.generator.metadata.protocols.LiverMri
import edu.washu.tag.generator.util.RandomGenUtils
import org.testng.annotations.Test

import java.nio.file.Files

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class OutputHandlerTest {

    @Test
    void testManifestMapsChosenOptionToMessageControlIds() {
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'K74.60', codeMeaning: 'Unspecified cirrhosis of liver')
        ])
        final SpecificationParameters specificationParameters = new SpecificationParameters(
            numPatients: 0,
            generateRadiologyReports: true,
            includePixelData: false,
            reportGeneratorImplementation: new CannedGenerator(),
            protocols: [],
            cohorts: [new SpecializedCohort(
                name: 'hcc',
                numPatients: 100,
                trajectory: [new StudyRequest(
                    name: 'diagnostic_mri',
                    protocol: new LiverMri(),
                    reportClasses: ['LiRadsCtMriDiagnostic'],
                    diagnosisCodes: ['K74.60'],
                    additionalGenerationContext: 'Base context.',
                    additionalGenerationContextOptions: ['common': 90, 'rare': 10]
                )]
            )]
        )
        specificationParameters.postprocess()

        final File datasetRoot = Files.createTempDirectory('output-handler-test').toFile()
        BatchProcessor.initDirs(datasetRoot.path)

        final List<Patient> patients = []
        new BatchRequest(id: 0, numPatients: 100, cohortName: 'hcc').generateBatchWithHandler(
            new BatchRequestContext()
                .specificationParameters(specificationParameters)
                .generationCache(GenerationCache.initInstance(specificationParameters))
                .idOffsets(new IdOffsets())
                .patientRandomizers(RandomGenUtils.setupWeightedLottery([(new DefaultPatientRandomizer() as PatientRandomizer): 1]))
                .handler({ Patient patient -> patients << patient })
        )

        final OutputHandler outputHandler = new OutputHandler(0)
        patients.each { outputHandler.writeHl7ForPatient(it) }
        outputHandler.writeManifest()

        final Map manifest = new ObjectMapper().readValue(new File(BatchProcessor.hl7Output, 'batch_0/manifest.json'), Map)
        final Map options = manifest.hcc.diagnostic_mri as Map
        // Null-safe: with a 90/10 weighting, a vanishingly small but nonzero chance exists that the rarer
        // option is never sampled at all across a single run's patients, in which case its key is absent.
        final List<String> commonIds = (options.common ?: []) as List<String>
        final List<String> rareIds = (options.rare ?: []) as List<String>
        assertEquals(commonIds.size() + rareIds.size(), 100)

        final Set<String> expectedIds = patients.collect { it.studies[0].radReport.messageControlId }
        final Set<String> manifestIds = (commonIds + rareIds) as Set
        assertEquals(manifestIds, expectedIds)
        assertTrue(commonIds.size() > rareIds.size(), options.toString())
    }

    @Test
    void testManifestIncludesStepsWithoutWeightedOptions() {
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'K74.60', codeMeaning: 'Unspecified cirrhosis of liver')
        ])
        final SpecificationParameters specificationParameters = new SpecificationParameters(
            numPatients: 0,
            generateRadiologyReports: true,
            includePixelData: false,
            reportGeneratorImplementation: new CannedGenerator(),
            protocols: [],
            cohorts: [new SpecializedCohort(
                name: 'hcc',
                numPatients: 20,
                trajectory: [new StudyRequest(
                    name: 'diagnostic_mri',
                    protocol: new LiverMri(),
                    reportClasses: ['LiRadsCtMriDiagnostic'],
                    diagnosisCodes: ['K74.60'],
                    additionalGenerationContext: 'Base context, no options.'
                )]
            )]
        )
        specificationParameters.postprocess()

        final File datasetRoot = Files.createTempDirectory('output-handler-test').toFile()
        BatchProcessor.initDirs(datasetRoot.path)

        final List<Patient> patients = []
        new BatchRequest(id: 0, numPatients: 20, cohortName: 'hcc').generateBatchWithHandler(
            new BatchRequestContext()
                .specificationParameters(specificationParameters)
                .generationCache(GenerationCache.initInstance(specificationParameters))
                .idOffsets(new IdOffsets())
                .patientRandomizers(RandomGenUtils.setupWeightedLottery([(new DefaultPatientRandomizer() as PatientRandomizer): 1]))
                .handler({ Patient patient -> patients << patient })
        )

        final OutputHandler outputHandler = new OutputHandler(0)
        patients.each { outputHandler.writeHl7ForPatient(it) }
        outputHandler.writeManifest()

        final Map manifest = new ObjectMapper().readValue(new File(BatchProcessor.hl7Output, 'batch_0/manifest.json'), Map)
        final Map options = manifest.hcc.diagnostic_mri as Map
        final List<String> ids = options.get('Base context, no options.') as List<String>

        final Set<String> expectedIds = patients.collect { it.studies[0].radReport.messageControlId }
        assertEquals(ids as Set, expectedIds)
    }

    /** Stands in for the LLM, mirroring LiRadsReportTest's CannedLiRadsGenerator. */
    static class CannedGenerator extends CyclicVariedGenerator {

        @Override
        boolean honorsRequestedReportClasses() {
            true
        }

        @Override
        protected List<PatientOutput> formBaseReports(List<Patient> patients, boolean temporalHeartbeat) {
            patients.collect { patient ->
                new PatientOutput(
                    patientId: patient.epicMrn,
                    generatedReports: patient.studies.collect { study ->
                        final LiRadsReport report = ReportRegistry.randomReportClass(study).getDeclaredConstructor().newInstance() as LiRadsReport
                        report.setUid(study.studyInstanceUid)
                        report.getUserMessage(study, null)
                        report.setExamination(study.simpleDescription)
                        report.setHistory('Test history.')
                        report.setTechnique('Test technique.')
                        report.setComparison('None.')
                        report.setFindings('Test findings.')
                        report.setImpression('See findings.')
                        report.setCategory('LR-5')
                        report as GeneratedReport
                    }
                )
            }
        }

        @Override
        protected overwriteReport(RadiologyReport reportToOverwrite, Class<? extends GeneratedReport> reportClass) {
            throw new UnsupportedOperationException()
        }

    }

}
