package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CX
import ca.uhn.hl7v2.model.v281.datatype.DTM
import ca.uhn.hl7v2.model.v281.datatype.PL
import ca.uhn.hl7v2.model.v281.datatype.XCN
import ca.uhn.hl7v2.model.v281.segment.PV1
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

class Pv1Generator extends SegmentGenerator<PV1> {

    private static final String HOSP = 'HOSP'

    @Override
    void generateSegment(RadiologyReport radReport, PV1 baseSegment) {
        baseSegment.getPv12_PatientClass().getCwe1_Identifier().setValue('O')
        final PL patientLocation = baseSegment.getPv13_AssignedPatientLocation()
        patientLocation.getPl1_PointOfCare().getHd1_NamespaceID().setValue('ABC RAD5')
        patientLocation.getPl2_Room().getHd1_NamespaceID().setValue('ABC RAD5 R-5005')
        patientLocation.getPl3_Bed().getHd1_NamespaceID().setValue('ABC RAD5 R-5005')
        patientLocation.getPl4_Facility().getHd1_NamespaceID().setValue('ABC') // TODO: these are too hardcoded

        radReport.attendingDoctors.eachWithIndex{ doctor, index ->
            final XCN hl7Doc = baseSegment.getPv17_AttendingDoctor(index)
            doctor.toXcn(hl7Doc, true, true)
            hl7Doc.getXcn13_IdentifierTypeCode().setValue(HOSP)
        }

        baseSegment.getPv110_HospitalService().getCwe1_Identifier().setValue('RAD')
        encodeVisitNumber(radReport, baseSegment.getPv119_VisitNumber())
        encodeAdmitDateTime(radReport, baseSegment.getPv144_AdmitDateTime()) // TODO: what time to use?
        baseSegment.getPv151_VisitIndicator().getCwe1_Identifier().setValue('V')
    }

    protected void encodeVisitNumber(RadiologyReport radiologyReport, CX visitNumber) {
        visitNumber.getCx1_IDNumber().setValue(radiologyReport.visitNumber)
    }

    protected void encodeAdmitDateTime(RadiologyReport radiologyReport, DTM admitDateTime) {
        admitDateTime.setValue(TimeUtils.toHl7(radiologyReport.reportDateTime))
    }

}
