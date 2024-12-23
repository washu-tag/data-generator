package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CX
import ca.uhn.hl7v2.model.v281.datatype.DTM
import edu.washu.tag.generator.metadata.RadiologyReport

class Pv1GeneratorHistorical extends Pv1Generator {

    @Override
    protected void encodeVisitNumber(RadiologyReport radiologyReport, CX visitNumber) {
        super.encodeVisitNumber(radiologyReport, visitNumber)
        radiologyReport.patientIds[0].assigningAuthority.toHd(visitNumber.getCx4_AssigningAuthority())
        visitNumber.getCx5_IdentifierTypeCode().setValue('VN')
    }

    @Override
    protected void encodeAdmitDateTime(RadiologyReport radiologyReport, DTM admitDateTime) {

    }

}
