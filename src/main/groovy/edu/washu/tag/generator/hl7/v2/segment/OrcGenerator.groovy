package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.PL
import ca.uhn.hl7v2.model.v281.datatype.XCN
import ca.uhn.hl7v2.model.v281.segment.ORC
import ca.uhn.hl7v2.util.DeepCopy
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.GenerationConstants
import edu.washu.tag.generator.util.TimeUtils

import java.time.LocalDateTime

class OrcGenerator extends SegmentGenerator<ORC> {

    @Override
    void generateSegment(RadiologyReport radReport, ORC baseSegment) {
        baseSegment.getOrc1_OrderControl().setValue('RE') // 'Observations/Performed Service to follow'
        DeepCopy.copy(radReport.placerOrderNumber, baseSegment.getOrc2_PlacerOrderNumber())
        DeepCopy.copy(radReport.fillerOrderNumber, baseSegment.getOrc3_FillerOrderNumber())

        baseSegment.getOrc5_OrderStatus().setValue(radReport.orcStatus.currentText)

        final LocalDateTime reportDateTime = radReport.reportDateTime
        final Terser terser = new Terser(baseSegment.getMessage())
        terser.set('/.ORC-7-4', TimeUtils.toHl7(reportDateTime.minusSeconds(1)))
        terser.set('/.ORC-7-5', TimeUtils.toHl7(reportDateTime))
        terser.set('/.ORC-7-6', 'O')
        radReport.setDeliverToLocation(baseSegment.getOrc7_DeliverToLocation(0))

        baseSegment.getOrc9_DateTimeOfTransaction().setValue(TimeUtils.toHl7(reportDateTime))

        radReport.technician?.toXcn(baseSegment.getOrc10_EnteredBy(0), true)

        final XCN orderer = baseSegment.getOrc12_OrderingProvider(0)
        radReport.orderingProvider.toXcn(orderer, true, true)
        orderer.getXcn13_IdentifierTypeCode().setValue('HOSP')

        final PL entererLocation = baseSegment.getOrc13_EntererSLocation()
        entererLocation.getPl1_PointOfCare().getHd1_NamespaceID().setValue(GenerationConstants.MAIN_HOSPITAL + '_R')
        entererLocation.getPl4_Facility().getHd1_NamespaceID().setValue('RS50')
        entererLocation.getPl9_LocationDescription().setValue('RAD5')

        baseSegment.getOrc14_CallBackPhoneNumber(0).getXtn1_TelephoneNumber().setValue('(555)555-5555')
        baseSegment.getOrc29_OrderType().getCwe1_Identifier().setValue('O')
        baseSegment.getOrc30_EntererAuthorizationMode().getCne1_Identifier().setValue('Electr')
    }

}
