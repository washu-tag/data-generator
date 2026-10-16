package edu.washu.tag.generator.ai.catalog

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.BatchRequestContext
import edu.washu.tag.generator.CustomGeneratedReportGuarantee
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import edu.washu.tag.generator.hl7.v2.CyclicVariedGenerator
import edu.washu.tag.generator.hl7.v2.FixedSampleReportGenerator
import edu.washu.tag.generator.hl7.v2.ReportGenerator
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.Diagnosis
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.metadata.cohorting.StudyRequest
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.ContrastAgent
import edu.washu.tag.generator.metadata.patient.DefaultPatientRandomizer
import edu.washu.tag.generator.metadata.patient.PatientRandomizer
import edu.washu.tag.generator.metadata.protocols.LiverCt
import edu.washu.tag.generator.metadata.protocols.LiverMri
import edu.washu.tag.generator.metadata.protocols.MriComplexProtocol
import edu.washu.tag.generator.metadata.protocols.Ultrasound
import edu.washu.tag.generator.util.RandomGenUtils
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertThrows
import static org.testng.Assert.assertTrue
import static org.testng.Assert.expectThrows

class LiRadsReportTest {

    private static final List<Class<? extends LiRadsReport>> US_CLASSES = [LiRadsUsSurveillance, LiRadsUs2024Surveillance]
    private static final List<Class<? extends LiRadsReport>> CT_MRI_CLASSES = [LiRadsCtMriDiagnostic, LiRadsCtMriNonradiationTra, LiRadsCtMriRadiationTra]

    @Test
    void testApplicability() {
        US_CLASSES.each { reportClass ->
            assertApplicable(reportClass, [Ultrasound], [LiverMri, LiverCt, MriComplexProtocol])
        }
        CT_MRI_CLASSES.each { reportClass ->
            assertApplicable(reportClass, [LiverMri, LiverCt], [Ultrasound, MriComplexProtocol])
        }
    }

    @Test
    void testLookupReportClass() {
        assertEquals(ReportRegistry.lookupReportClass('LiRadsCtMriRadiationTra'), LiRadsCtMriRadiationTra)
        final IllegalArgumentException exception = expectThrows(IllegalArgumentException) {
            ReportRegistry.lookupReportClass('LiRadsReport')
        }
        assertTrue(exception.message.contains('Known report classes'), exception.message)
    }

    @Test
    void testRequestedReportClassMustApply() {
        final Study study = new Study(protocol: new Ultrasound(), simpleDescription: 'ultrasound', requestedReportClasses: [LiRadsCtMriDiagnostic])
        assertThrows(IllegalStateException) { ReportRegistry.randomReportClass(study) }
    }

    @Test
    void testRequestedReportClassesRestrictChoice() {
        final Study study = new Study(protocol: new Ultrasound(), requestedReportClasses: [LiRadsUs2024Surveillance])
        20.times {
            assertEquals(ReportRegistry.randomReportClass(study), LiRadsUs2024Surveillance)
        }
    }

    @Test
    void testTraAncillaryFeaturesOnlyForMri() {
        [LiRadsCtMriNonradiationTra, LiRadsCtMriRadiationTra].each { reportClass ->
            final LiRadsReport report = reportClass.getDeclaredConstructor().newInstance()
            final String mriPrompt = report.getUserMessage(new Study(protocol: new LiverMri()), null)
            final String ctPrompt = report.getUserMessage(new Study(protocol: new LiverCt()), null)
            assertTrue(mriPrompt.toLowerCase().contains('diffusion restriction or mild-moderate t2 hyperintensity'), reportClass.simpleName)
            assertFalse(ctPrompt.toLowerCase().contains('diffusion restriction'), reportClass.simpleName)
            assertFalse(ctPrompt.contains('T2 hyperintensity'), reportClass.simpleName)
        }
    }

    @Test
    void testUnknownReportClassRejectedAtConfig() {
        final IllegalArgumentException exception = expectThrows(IllegalArgumentException) {
            specificationWith(new CannedLiRadsGenerator(), [step('mri', new LiverMri(), ['LiRadsCtMriDiagnostc'])]).postprocess()
        }
        assertTrue(exception.message.contains("step 'mri'"), exception.message)
    }

    @Test
    void testReportClassesRequireHonoringGenerator() {
        final IllegalArgumentException exception = expectThrows(IllegalArgumentException) {
            specificationWith(new FixedSampleReportGenerator(), [step('mri', new LiverMri(), ['LiRadsCtMriDiagnostic'])]).postprocess()
        }
        assertTrue(exception.message.contains('customReportFraction'), exception.message)
    }

    @Test
    void testGuaranteeVersionMustBeSupported() {
        final SpecificationParameters specificationParameters = specificationWith(new CannedLiRadsGenerator(), [step('mri', new LiverMri(), [])])
        specificationParameters.setCustomReportGuarantees([
            new CustomGeneratedReportGuarantee(reportClass: LiRadsCtMriDiagnostic, reportVersions: [ReportVersion.V2_7, ReportVersion.V2_3])
        ])
        final IllegalArgumentException exception = expectThrows(IllegalArgumentException) {
            specificationParameters.postprocess()
        }
        assertTrue(exception.message.contains('V2_3'), exception.message)
    }

    @Test
    void testCohortEndToEnd() {
        // seed the code cache so the cohort diagnosis code resolves without querying the NLM API
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'K74.60', codeMeaning: 'Unspecified cirrhosis of liver')
        ])
        final SpecificationParameters specificationParameters = specificationWith(new CannedLiRadsGenerator(), [
            step('surveillance', new Ultrasound(), ['LiRadsUsSurveillance']),
            step('diagnostic_mri', new LiverMri(), ['LiRadsCtMriDiagnostic']),
            step('post_tace_ct', new LiverCt(), ['LiRadsCtMriNonradiationTra']),
            step('post_tare_mri', new LiverMri(contrastAgent: ContrastAgent.GADOXETATE), ['LiRadsCtMriRadiationTra'])
        ])
        final List<Patient> patients = []
        new BatchRequest(id: 0, numPatients: 5, cohortName: 'hcc').generateBatchWithHandler(
            new BatchRequestContext()
                .specificationParameters(specificationParameters)
                .generationCache(GenerationCache.initInstance(specificationParameters))
                .idOffsets(new IdOffsets())
                .patientRandomizers(RandomGenUtils.setupWeightedLottery([(new DefaultPatientRandomizer() as PatientRandomizer): 1]))
                .handler({ Patient patient -> patients << patient })
        )

        assertEquals(patients.size(), 5)
        patients.each { patient ->
            assertEquals(
                patient.studies*.radReport*.generatedReport*.class,
                [LiRadsUsSurveillance, LiRadsCtMriDiagnostic, LiRadsCtMriNonradiationTra, LiRadsCtMriRadiationTra]
            )
            patient.studies.each { study ->
                final RadiologyReport radReport = study.radReport
                assertTrue(radReport.hl7Version in [ReportVersion.V2_4, ReportVersion.V2_7], radReport.hl7Version.toString())
                radReport.produceReport(patient, study).encode()
                final String reportText = radReport.getReportTextForQueryExport()
                assertTrue(reportText.contains('IMPRESSION:'), reportText)
                if (study.protocol instanceof Ultrasound) {
                    assertTrue(reportText.contains('US LI-RADS REFERENCE:'), reportText)
                } else {
                    assertEquals(study.bodyPartExamined, BodyPart.LIVER)
                }
            }
        }
    }

    private static void assertApplicable(Class<? extends LiRadsReport> reportClass, List<Class<? extends Protocol>> applicable, List<Class<? extends Protocol>> notApplicable) {
        final LiRadsReport report = reportClass.getDeclaredConstructor().newInstance()
        applicable.each { protocolClass ->
            assertTrue(report.checkApplicability(new Study(protocol: protocolClass.getDeclaredConstructor().newInstance())), "${reportClass.simpleName} on ${protocolClass.simpleName}")
        }
        notApplicable.each { protocolClass ->
            assertFalse(report.checkApplicability(new Study(protocol: protocolClass.getDeclaredConstructor().newInstance())), "${reportClass.simpleName} on ${protocolClass.simpleName}")
        }
    }

    private static StudyRequest step(String name, Protocol protocol, List<String> reportClasses) {
        new StudyRequest(name: name, protocol: protocol, reportClasses: reportClasses, studyOffsetMin: 30, studyOffsetMax: 60)
    }

    private static SpecificationParameters specificationWith(ReportGenerator generator, List<StudyRequest> trajectory) {
        trajectory[0].setDiagnosisCodes(['K74.60']) // the default fixed-for-patient code strategy needs codes on the first step
        new SpecificationParameters(
            numPatients: 0,
            generateRadiologyReports: true,
            includePixelData: false,
            reportGeneratorImplementation: generator,
            protocols: [],
            cohorts: [new SpecializedCohort(name: 'hcc', numPatients: 5, trajectory: trajectory)]
        )
    }

    /**
     * Stands in for the LLM: chooses report classes exactly as the GPT generator's cohort path does, then fills in fixed
     * content so the rest of the HL7 pipeline runs unchanged.
     */
    static class CannedLiRadsGenerator extends CyclicVariedGenerator {

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
                        report.setHistory('62-year-old with hepatitis C cirrhosis.')
                        report.setTechnique('Multiphase imaging of the liver.')
                        report.setComparison('None.')
                        report.setFindings('Cirrhotic liver morphology. Segment 7 observation.')
                        report.setImpression('See findings.')
                        if (report instanceof LiRadsUsSurveillance) {
                            report.setCategory('US-2')
                            report.setVisualizationScore('B')
                        } else {
                            report.setCategory('LR-5')
                        }
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
