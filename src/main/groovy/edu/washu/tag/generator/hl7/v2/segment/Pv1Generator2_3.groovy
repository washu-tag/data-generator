package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.datatype.CX
import ca.uhn.hl7v2.model.v281.datatype.PL
import edu.washu.tag.generator.metadata.RadiologyReport

class Pv1Generator2_3 extends Pv1Generator {

    /*
      Observations from real data to explain the choices made here:

      * PV1-2 has similar one letter identifiers like "O" for outpatient, so I'll leave it hardcoded.
      * PV1-3 differs in format from what I've seen elsewhere. It looks like ^123^^^^^^^ABC RM 123
      * PV1-7 usually looks like 123^LAST^FIRST^MIDDLE^^^DEGREE . Some observations:
         * The ID in the first component can be empty
         * The middle name can be a full middle name, an initial with or without a period, or empty
         * As long as there is a doctor encoded in PV1-7, the 7th component can be a degree like MD or M.D., but
           it is always encoded.
         * For the same doctor, I have seen a different ID (and degree was formatted slightly differently)
       * PV1-10, PV1-19, and PV1-51 are not present like in later reports
     */

    @Override
    protected void encodePatientLocation(PL patientLocation) {
        patientLocation.getPl2_Room().getHd1_NamespaceID().setValue('123')
        patientLocation.getPl9_LocationDescription().setValue('ABC RM 123')
    }

    @Override
    protected void encodeHospitalService(CWE hospitalService) {

    }

    @Override
    protected void encodeVisitNumber(RadiologyReport radiologyReport, CX visitNumber) {

    }

    @Override
    protected void encodeVisitIndicator(CWE visitIndicator) {

    }

}
