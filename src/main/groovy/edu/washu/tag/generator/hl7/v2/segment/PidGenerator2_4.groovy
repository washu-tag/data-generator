package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CX
import ca.uhn.hl7v2.model.v281.datatype.DTM
import ca.uhn.hl7v2.model.v281.datatype.ST
import ca.uhn.hl7v2.model.v281.segment.PID
import ca.uhn.hl7v2.util.DeepCopy
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.util.TimeUtils

class PidGenerator2_4 extends PidGenerator {

    @Override
    protected void addDob(Patient patient, DTM dobElement) {
        dobElement.setValue(TimeUtils.toHl7(patient.getDateOfBirth().atStartOfDay()))
    }

    @Override
    protected void addRace(Race race, ST raceElement) {
        raceElement.setValue(race.getHl7v24Encoding())
    }

    @Override
    protected void encodeLateFields(RadiologyReport radReport, PID baseSegment) {
        baseSegment.getPid13_PhoneNumberHome(0).getXtn1_TelephoneNumber().setValue('5555555555')

        baseSegment.getPid16_MaritalStatus().getCwe1_Identifier().setValue('S')

        final CX pid18 = baseSegment.getPid18_PatientAccountNumber()
        DeepCopy.copy(baseSegment.getPid3_PatientIdentifierList(0), pid18)
        pid18.getCx5_IdentifierTypeCode().setValue('PN')

        baseSegment.getPid31_IdentityUnknownIndicator().setValue('')
    }

}
