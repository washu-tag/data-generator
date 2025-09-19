package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.segment.MSH
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

class MshGenerator extends SegmentGenerator<MSH> {

    @Override
    void generateSegment(RadiologyReport radReport, MSH msh) {
        msh.getMsh1_FieldSeparator().setValue('|')
        msh.getMsh2_EncodingCharacters().setValue('^~\\&')
        msh.getMsh3_SendingApplication().getHd1_NamespaceID().setValue('SOMERIS')
        msh.getMsh4_SendingFacility().getHd1_NamespaceID().setValue('ABCHOSP')
        msh.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue('SOMEAPP')
        msh.getMsh6_ReceivingFacility().getHd1_NamespaceID().setValue('ABC_HOSP_DEPT_X')
        msh.getMsh7_DateTimeOfMessage().setValue(TimeUtils.toHl7(radReport.reportDateTime))
        msh.getMsh8_Security().setValue('TBD')
        msh.getMsh9_MessageType().getMsg1_MessageCode().setValue('ORU')
        msh.getMsh9_MessageType().getMsg2_TriggerEvent().setValue('R01') // intentionally skipping MSH-9.3
        msh.getMsh10_MessageControlID().setValue(radReport.messageControlId)
        msh.getMsh11_ProcessingID().getPt1_ProcessingID().setValue('P')
        msh.getMsh12_VersionID().getVid1_VersionID().setValue(radReport.getHl7Version().hl7Version)
    }
    
}
