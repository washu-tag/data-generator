package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.segment.OBR
import ca.uhn.hl7v2.util.DeepCopy
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.hl7.v2.model.TransportationMode
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

import java.time.LocalDateTime

class ObrGenerator2_4 extends ObrGenerator {
    
    private static final String POWERSCRIBE = 'PSCRIB'

    @Override
    void generateSegment(RadiologyReport radReport, OBR baseSegment) {
        final LocalDateTime reportTime = radReport.reportDateTime
        final CodedTriplet procedureCode = ProcedureCode.lookup(radReport.study.procedureCodeId).codedTriplet
        final String numericProcedureCode = procedureCode.codeValue.replaceAll('\\D', '')
        final String procedureText = "${numericProcedureCode} ${procedureCode.codeMeaning}".trim()

        DeepCopy.copy(radReport.placerOrderNumber, baseSegment.getObr2_PlacerOrderNumber())
        DeepCopy.copy(radReport.fillerOrderNumber, baseSegment.getObr3_FillerOrderNumber())

        final CWE universalServiceIdentifier = baseSegment.getObr4_UniversalServiceIdentifier()
        universalServiceIdentifier.getCwe1_Identifier().setValue(procedureCode.codeValue)
        universalServiceIdentifier.getCwe2_Text().setValue(procedureText)
        universalServiceIdentifier.getCwe3_NameOfCodingSystem().setValue(procedureCode.codingSchemeDesignator)

        baseSegment.getObr5_DeliverToLocation().setValue('URGENT')
        baseSegment.getObr7_ObservationDateTime().setValue(TimeUtils.toHl7(radReport.study.studyDateTime()))
        baseSegment.getObr10_CollectorIdentifier(0).getXcn1_PersonIdentifier().setValue(POWERSCRIBE)

        Terser.set(baseSegment, 15, 0, 5, 1, ' ')
        radReport.orderingProvider.toXcn(baseSegment.getObr16_OrderingProvider(0), true)
        baseSegment.getObr21_FillerField2().setValue(numericProcedureCode)
        Terser.set(baseSegment, 21, 0, 2, 1, procedureCode.alternateText)

        baseSegment.getObr22_ResultsRptStatusChngDateTime().setValue(TimeUtils.toHl7(reportTime))
        baseSegment.getObr24_DiagnosticServSectID().setValue(procedureCode.codeValue.replaceAll('\\d', ''))

        Terser.set(baseSegment, 27, 0, 1, 1, '1')
        Terser.set(baseSegment, 27, 0, 4, 1, TimeUtils.toHl7(reportTime))
        Terser.set(baseSegment, 27, 0, 6, 1, 'U')
        Terser.set(baseSegment, 27, 0, 8, 1, 'URGENT')
        baseSegment.getObr28_ResultCopiesTo(0).getXcn1_PersonIdentifier().setValue('       ')
        baseSegment.getObr30_TransportationMode().setValue(serializeTransportationMode(radReport))

        addReasonForStudy(radReport, baseSegment)

        final NameEncoder nameEncoder = new NameEncoder(radReport, baseSegment)
        nameEncoder.encodePrincipalResultInterpreter(radReport.principalInterpreter)
        nameEncoder.encodeAssistantResultInterpreter(radReport.assistantInterpreters)

        baseSegment.getObr35_Transcriptionist(0).getNdl1_Name().getCnn1_IDNumber().setValue(POWERSCRIBE)
        baseSegment.getObr44_ProcedureCode().getCne1_Identifier().setValue(numericProcedureCode)
        // TODO: could implement OBR-45
    }

    // The accession number check is because I wanted to malform the data sometimes to match the real data
    // but without a new source of randomness
    private String serializeTransportationMode(RadiologyReport radiologyReport) {
        final TransportationMode transportationMode = radiologyReport.transportationMode
        if (transportationMode == null) {
            return '{}'
        }
        if (transportationMode == TransportationMode.PORTABLE && radiologyReport.study.accessionNumber.endsWith('1')) {
            return 'port'
        }
        return transportationMode.serialized2_4
    }

}
