package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.datatype.CX
import ca.uhn.hl7v2.model.v281.datatype.DTM
import ca.uhn.hl7v2.model.v281.datatype.PL
import ca.uhn.hl7v2.model.v281.segment.PV1
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

class Pv1Generator extends SegmentGenerator<PV1> {

    @Override
    void generateSegment(RadiologyReport radReport, PV1 baseSegment) {
        baseSegment.getPv12_PatientClass().getCwe1_Identifier().setValue('O')
        encodePatientLocation(baseSegment.getPv13_AssignedPatientLocation())

        radReport.attendingDoctors.eachWithIndex{ doctor, index ->
            radReport.getDoctorEncoder().encode(doctor, baseSegment.getPv17_AttendingDoctor(index))
        }

        encodeHospitalService(baseSegment.getPv110_HospitalService())
        encodeVisitNumber(radReport, baseSegment.getPv119_VisitNumber())
        encodeAdmitDateTime(radReport, baseSegment.getPv144_AdmitDateTime()) // TODO: what time to use?
        encodeVisitIndicator(baseSegment.getPv151_VisitIndicator())
    }

    protected void encodePatientLocation(PL patientLocation) {
        patientLocation.getPl1_PointOfCare().getHd1_NamespaceID().setValue('ABC RAD5')
        patientLocation.getPl2_Room().getHd1_NamespaceID().setValue('ABC RAD5 R-5005')
        patientLocation.getPl3_Bed().getHd1_NamespaceID().setValue('ABC RAD5 R-5005')
        patientLocation.getPl4_Facility().getHd1_NamespaceID().setValue('ABC') // TODO: these are too hardcoded
    }

    protected void encodeHospitalService(CWE hospitalService) {
        hospitalService.getCwe1_Identifier().setValue('RAD')
    }

    protected void encodeVisitNumber(RadiologyReport radiologyReport, CX visitNumber) {
        visitNumber.getCx1_IDNumber().setValue(radiologyReport.visitNumber)
    }

    protected void encodeAdmitDateTime(RadiologyReport radiologyReport, DTM admitDateTime) {
        admitDateTime.setValue(TimeUtils.toHl7(radiologyReport.reportDateTime))
    }

    protected void encodeVisitIndicator(CWE visitIndicator) {
        visitIndicator.getCwe1_Identifier().setValue('V')
    }

}
