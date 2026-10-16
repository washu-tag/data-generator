package edu.washu.tag.generator.metadata.series

import edu.washu.tag.generator.BatchRequest
import edu.washu.tag.generator.BatchRequestContext
import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import edu.washu.tag.generator.metadata.Diagnosis
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.metadata.cohorting.StudyRequest
import edu.washu.tag.generator.metadata.patient.DefaultPatientRandomizer
import edu.washu.tag.generator.metadata.patient.PatientRandomizer
import edu.washu.tag.generator.metadata.protocols.LiverCt
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64
import edu.washu.tag.generator.metadata.seriesTypes.ct.MultiphaseCt
import edu.washu.tag.generator.metadata.seriesTypes.ct.Topogram
import edu.washu.tag.generator.util.RandomGenUtils
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

class CtSeriesTest {

    @Test
    void testDescriptionAgreesWithEncodedAttributes() {
        final List<CtSeries> ctSeries = generateLiverCtSeries(20)
        final List<CtSeries> namedSeries = ctSeries.findAll { it.seriesType instanceof Topogram || it.seriesType instanceof MultiphaseCt }
        assertFalse(namedSeries.isEmpty())
        assertTrue(namedSeries.any { it.seriesDescription.split(/\s+/).size() >= 3 && it.seriesType instanceof MultiphaseCt }, 'expected some Siemens multiphase descriptions naming thickness and kernel')

        namedSeries.each { series ->
            final Attributes attributes = new Attributes()
            series.encode(attributes)
            final String description = series.seriesDescription
            assertFalse(description.contains('%'), description)
            assertNotNull(attributes.getString(Tag.SliceThickness), description)
            assertEquals(attributes.getString(Tag.SliceThickness), String.valueOf(series.sliceThickness), description)

            final boolean siemens = series.scanner instanceof SiemensBiographTruePoint64
            if (series.seriesType instanceof Topogram || siemens) {
                assertEquals(attributes.getString(Tag.ConvolutionKernel), series.convolutionKernel, description)
            } else {
                assertNull(attributes.getString(Tag.ConvolutionKernel), description)
            }

            final List<String> tokens = description.split(/\s+/) as List<String>
            if (tokens.size() >= 3 && tokens[-1] == series.convolutionKernel) {
                assertEquals(tokens[-2], attributes.getString(Tag.SliceThickness), description)
            }
            if (series.seriesType instanceof Topogram) {
                assertEquals(description, "Topogram ${attributes.getString(Tag.SliceThickness)} ${attributes.getString(Tag.ConvolutionKernel)}".toString())
            }
        }
    }

    private static List<CtSeries> generateLiverCtSeries(int numPatients) {
        // seed the code cache so the cohort diagnosis code resolves without querying the NLM API
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'K74.60', codeMeaning: 'Unspecified cirrhosis of liver')
        ])
        final SpecificationParameters specificationParameters = new SpecificationParameters(
            numPatients: 0,
            generateRadiologyReports: false,
            includePixelData: false,
            protocols: [],
            cohorts: [new SpecializedCohort(name: 'liver_ct', numPatients: numPatients, trajectory: [
                new StudyRequest(name: 'ct', protocol: new LiverCt(precontrastFraction: 1), diagnosisCodes: ['K74.60'])
            ])]
        )
        specificationParameters.postprocess()
        final List<Patient> patients = []
        new BatchRequest(id: 0, numPatients: numPatients, cohortName: 'liver_ct').generateBatchWithHandler(
            new BatchRequestContext()
                .specificationParameters(specificationParameters)
                .generationCache(GenerationCache.initInstance(specificationParameters))
                .idOffsets(new IdOffsets())
                .patientRandomizers(RandomGenUtils.setupWeightedLottery([(new DefaultPatientRandomizer() as PatientRandomizer): 1]))
                .handler({ Patient patient -> patients << patient })
        )
        patients*.studies.flatten()*.series.flatten().findAll { it instanceof CtSeries } as List<CtSeries>
    }

}
