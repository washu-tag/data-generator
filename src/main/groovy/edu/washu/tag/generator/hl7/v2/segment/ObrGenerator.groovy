package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.datatype.XCN
import ca.uhn.hl7v2.model.v281.segment.OBR
import ca.uhn.hl7v2.util.DeepCopy
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.GenerationConstants
import edu.washu.tag.generator.util.TimeUtils

class ObrGenerator extends SegmentGenerator<OBR> {

    private static final List<String> primaryModalities = [
            'CR', 'CT', 'US', 'MR', 'MG', 'DX', 'RF', 'XA', 'NM', 'PT', 'OT'
    ]

    @Override
    void generateSegment(RadiologyReport radReport, OBR baseSegment) {
        final Study study = radReport.study
        final CodedTriplet procedureCode = ProcedureCode.lookup(study.procedureCodeId).codedTriplet

        baseSegment.getObr1_SetIDOBR().setValue('1')
        DeepCopy.copy(radReport.placerOrderNumber, baseSegment.getObr2_PlacerOrderNumber())
        DeepCopy.copy(radReport.fillerOrderNumber, baseSegment.getObr3_FillerOrderNumber())

        final CWE universalServiceIdentifier = baseSegment.getObr4_UniversalServiceIdentifier()
        universalServiceIdentifier.getCwe1_Identifier().setValue(procedureCode.codeValue)
        universalServiceIdentifier.getCwe2_Text().setValue(procedureCode.codeMeaning)
        universalServiceIdentifier.getCwe3_NameOfCodingSystem().setValue(procedureCode.codingSchemeDesignator)
        universalServiceIdentifier.getCwe5_AlternateText().setValue(procedureCode.alternateText.toUpperCase())

        baseSegment.getObr5_DeliverToLocation().setValue('O')
        baseSegment.getObr6_DeliverToLocationNumber2().setValue(
                TimeUtils.toHl7(
                        study.studyDateTime()
                )
        )
        baseSegment.getObr11_SpecimenActionCode().setValue('Hosp Perf')

        final Terser terser = new Terser(baseSegment.getMessage())
        if (study.bodyPartExamined != null) {
            terser.set('/.OBR-15-4', study.bodyPartExamined.dicomRepresentation)
        }

        final XCN orderer = baseSegment.getObr16_OrderingProvider(0)
        radReport.orderingProvider.toXcn(orderer, true, true)
        orderer.getXcn13_IdentifierTypeCode().setValue('HOSP')

        baseSegment.getObr17_OrderCallbackPhoneNumber(0).getXtn1_TelephoneNumber().setValue('(555)555-5555')
        baseSegment.getObr18_PlacerField1().setValue('CHEST')

        baseSegment.getObr19_PlacerField2().setValue("${GenerationConstants.MAIN_HOSPITAL} RAD DX")
        terser.set('/.OBR-19-4', GenerationConstants.MAIN_HOSPITAL)

        baseSegment.getObr20_FillerField1().setValue('GEXR5')
        baseSegment.getObr22_ResultsRptStatusChngDateTime().setValue(TimeUtils.toHl7(radReport.reportDateTime))
        baseSegment.getObr24_DiagnosticServSectID().setValue(derivePrimaryImagingModality(study))
        baseSegment.getObr25_ResultStatus().setValue(radReport.orcStatus.currentText)

        DeepCopy.copy(radReport.deliverToLocation, baseSegment.getObr27_DeliverToLocationNumber5(0))

        addReasonForStudy(radReport, baseSegment)

        final NameEncoder nameEncoder = new NameEncoder(radReport, baseSegment)
        nameEncoder.encodeAssistantResultInterpreter(radReport.assistantInterpreters)

        final Person tech = radReport.technician
        if (tech != null) {
            nameEncoder.encodeTechnician(tech)
        }

        baseSegment.getObr36_ScheduledDateTime().setValue(TimeUtils.toHl7(radReport.reportDateTime))
        DeepCopy.copy(universalServiceIdentifier, baseSegment.getObr44_ProcedureCode())
    }

    protected void addReasonForStudy(RadiologyReport radiologyReport, OBR baseSegment) {
        baseSegment.getObr31_ReasonForStudy(0).getCwe2_Text().setValue(radiologyReport.reasonForStudy) // TODO: duplicate reason into component 3 sometimes
    }

    static String derivePrimaryImagingModality(Study study) {
        final List<String> modalities = study.series*.modality.unique()
        if (modalities.size() == 1) {
            modalities[0]
        } else {
            modalities.find {
                it in primaryModalities
            } ?: modalities[0]
        }
    }

    protected static final class NameEncoder {
        private final DoctorEncoder doctorEncoder
        private final OBR baseSegment
        private final boolean malform

        NameEncoder(RadiologyReport radReport, OBR baseSegment) {
            doctorEncoder = radReport.getDoctorEncoder()
            this.baseSegment = baseSegment
            malform = radReport.malformInterpretersTechnician
        }

        protected void encodePrincipalResultInterpreter(Person person) {
            doctorEncoder.encode([person], baseSegment, 32, malform)
        }

        protected void encodeAssistantResultInterpreter(List<Person> people) {
            doctorEncoder.encode(people, baseSegment, 33, malform)
        }

        protected void encodeTechnician(Person person) {
            doctorEncoder.encode([person], baseSegment, 34, malform)
        }

        protected void encodeTranscriptionist(Person person) {
            doctorEncoder.encode([person], baseSegment, 35, malform)
        }
    }

}
