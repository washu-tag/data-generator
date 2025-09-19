package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.ST
import ca.uhn.hl7v2.model.v281.datatype.XAD
import ca.uhn.hl7v2.model.v281.segment.PID
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race

class PidGenerator2_3 extends PidGenerator {

    /*
      Observations from real data to explain the choices made here:

      * PID-1 is empty unlike 2.4 or 2.7 reports.
      * PID-2 has a patient id of some sort unlike the 2.4 or 2.7 reports. The id does seem consistent across time for
        an individual patient based on some spot checks.
      * PID-3 seems to be single-valued, unlike in later reports.
      * PID-3.1 does have what looks like an ID, but it doesn't match PID-2.
      * PID-3.2 and PID-3.3 have varying 1 digit numbers that I don't know how to interpret.
      * PID-3.4 is empty unlike in 2.4 or 2.7 reports.
      * PID-3.6 has what is probably a hospital identifier, but I've only seen a few values, so I'll just hardcode it until we need it.
      * PID-5 does seem to be consistently LAST^FIRST^MIDDLE^SUFFIX^^, where middle is either a full name or initial,
        and the suffix is optional. From spot checks, the patient name does seem to be consistently encoded across
        2.3 reports.
      * PID-7 is a DOB in the expected simple YYYYMMDD format.
      * PID-9 looks like LAST^FIRST^^^^, and it present sometimes, and other times not. Sometimes the usual name field looks like
        LAST^FIRST^^^^ with the longer name in the alias. Sometimes these differ by initial versus middle name. We don't have a use
        case for such varying name entry methods yet, so I will keep it simple for now.
      * PID-10 is empty
      * PID-11 is present, and it doesn't quite match the format I used elsewhere. I'll hardcode one format I've encountered here, but there's
        no guarantee that format is comprehensive of all examples.
      * PID-13 and PID-14 look like "(111) 111-1111 x" (with a literal x, presumably as an delimiter for phone number extension)
      * PID-18 does seem to have a value, but I'm not sure what it means. I'm going to use the legacy patient ID but with a prefix and suffix
        so that hopefully people will not see the link between PID-2 and PID-18 and think it was intended to be meaningful.
      * PID-30 has a value of "N" meaning "not deceased". Presumably this might be "Y" for some cases, but this is not something
        I can model right now, so I'll hardcode it as "N".
     */

    @Override
    protected void addSetId(PID baseSegment) {
        // empty in 2.3
    }

    @Override
    protected void addLegacyPatientId(PID baseSegment, Patient patient) {
        baseSegment.pid2_PatientID.setValue(patient.legacyPatientId)
    }

    @Override
    protected void addPatientName(PID baseSegment, Person patientName) {
        patientName.toXpn(baseSegment.getPid5_PatientName(0), true)
    }

    @Override
    protected void addRace(Race race, ST raceElement) {
        // empty in 2.3
    }

    @Override
    protected void addPatientAddress(RadiologyReport radReport, XAD patientAddress) {
        patientAddress.getXad1_StreetAddress().getSad1_StreetOrMailingAddress().setValue('123 STREET')
        patientAddress.getXad3_City().setValue('CITY CITY')
        patientAddress.getXad4_StateOrProvince().setValue('MO')
        patientAddress.getXad5_ZipOrPostalCode().setValue('61111')
    }

    @Override
    protected void encodeLateFields(RadiologyReport radReport, PID baseSegment) {
        final String phoneNumber = '(111) 111-1111 x'
        baseSegment.getPid13_PhoneNumberHome(0).getXtn1_TelephoneNumber().setValue(phoneNumber)
        baseSegment.getPid14_PhoneNumberBusiness(0).getXtn1_TelephoneNumber().setValue(phoneNumber)
        baseSegment.getPid18_PatientAccountNumber().getCx1_IDNumber().setValue('M' + radReport.patient.legacyPatientId + '3R')
        baseSegment.getPid19_SSNNumberPatient().setValue('111-11-1111')
        baseSegment.getPid30_PatientDeathIndicator().setValue('N')
    }

}
