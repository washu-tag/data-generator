package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.datatype.XCN
import ca.uhn.hl7v2.model.v281.segment.OBR
import ca.uhn.hl7v2.util.DeepCopy
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Person
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
        final CodedTriplet procedureCode = study.procedureCode

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

        final NameEncoder nameEncoder = new NameEncoder(baseSegment, radReport.malformInterpretersTechnician)
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

    protected class NameEncoder {
        private final OBR obr
        private final boolean malform

        protected NameEncoder(OBR obr, boolean malform) {
            this.obr = obr
            this.malform = malform
        }

        private void encodeValueViaTerser(int fieldId, int rep, int componentId, int subcomponentId, String value) {
            Terser.set(obr, fieldId, rep, componentId, subcomponentId, value)
        }

        protected void encodeValue(int fieldId, int rep, int pieceId, String value) {
            encodeValueViaTerser(
                fieldId,
                rep,
                malform ? pieceId : 1,
                malform ? 1 : pieceId,
                value
            )
        }

        protected void encodeNdlElement(int fieldId, List<Person> people) {
            people.eachWithIndex { person, i ->
                encodeValue(fieldId, i, 1, person.personIdentifier.toUpperCase())
                encodeValue(fieldId, i, 2, person.familyNameAlphabetic.toUpperCase())
                encodeValue(fieldId, i, 3, person.givenNameAlphabetic.toUpperCase())
                encodeValue(fieldId, i, 4, person.middleInitial()?.toUpperCase())
                if (person.getAssigningAuthority() != null) {
                    encodeValue(fieldId, i, 9, person.getAssigningAuthority().getNamespaceId())
                }
                if (malform && fieldId < 34) {
                    encodeValue(fieldId, i, 13, 'HOSP')
                }
            }
        }

        protected void encodePrincipalResultInterpreter(Person person) {
            encodeNdlElement(32, Collections.singletonList(person))
        }

        protected void encodeAssistantResultInterpreter(List<Person> person) {
            encodeNdlElement(33, person)
        }

        protected void encodeTechnician(Person person) {
            encodeNdlElement(34, Collections.singletonList(person))
        }
    }

}
