package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.DTM
import ca.uhn.hl7v2.model.v281.datatype.ST
import ca.uhn.hl7v2.model.v281.datatype.XAD
import ca.uhn.hl7v2.model.v281.datatype.XTN
import ca.uhn.hl7v2.model.v281.segment.PID
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.util.TimeUtils

class PidGenerator extends SegmentGenerator<PID> {

    @Override
    void generateSegment(RadiologyReport radReport, PID baseSegment) {
        final Patient patient = radReport.patient
        final Person patientName = patient.patientName.nameTypeCode('D') // code for 'customary name'
        baseSegment.getPid1_SetIDPID().setValue('1')

        radReport.patientIds.eachWithIndex { patientId, index ->
            patientId.encodeId(baseSegment.getPid3_PatientIdentifierList(index))
        }

        patientName.toXpn(baseSegment.getPid5_PatientName(0), true)

        addDob(patient, baseSegment.getPid7_DateTimeOfBirth())

        patient.sex.toCwe(baseSegment.getPid8_AdministrativeSex())
        if (radReport.includeAlias) {
            Terser.set(baseSegment, 9, 0, 1, 1, patientName.familyNameAlphabetic.toUpperCase())
            Terser.set(baseSegment, 9, 0, 2, 1, patientName.givenNameAlphabetic.toUpperCase())
        }

        final Race race = radReport.race
        if (race != null) {
            addRace(race, baseSegment.getPid10_Race(0).getCwe1_Identifier())
        }

        final XAD patientAddress = baseSegment.getPid11_PatientAddress(0)
        if (radReport.specifyAddress) {
            patientAddress.getXad1_StreetAddress().getSad1_StreetOrMailingAddress().setValue('123 STREET')
            patientAddress.getXad3_City().setValue('CITY CITY')
            patientAddress.getXad4_StateOrProvince().setValue('MO')
            patientAddress.getXad5_ZipOrPostalCode().setValue('61111')
            patientAddress.getXad9_CountyParishCode().getCwe1_Identifier().setValue('COUNTY')
        }
        patientAddress.getXad6_Country().setValue('USA')
        patientAddress.getXad7_AddressType().setValue('L')

        encodeLateFields(radReport, baseSegment)
    }

    protected void addDob(Patient patient, DTM dobElement) {
        dobElement.setValue(TimeUtils.toHl7(patient.getDateOfBirth()))
    }

    protected void addRace(Race race, ST raceElement) {
        raceElement.setValue(race.getHl7v27Encoding())
    }

    protected void encodeLateFields(RadiologyReport radReport, PID baseSegment) {
        if (!radReport.extendedPid) {
            return
        }

        final String phoneNumber = '(555)555-5555'
        final XTN phone1 = baseSegment.getPid13_PhoneNumberHome(0)
        phone1.getXtn1_TelephoneNumber().setValue(phoneNumber)
        phone1.getXtn2_TelecommunicationUseCode().setValue('P')
        phone1.getXtn3_TelecommunicationEquipmentType().setValue('H')
        final XTN phone2 = baseSegment.getPid13_PhoneNumberHome(1)
        phone2.getXtn1_TelephoneNumber().setValue(phoneNumber)
        phone2.getXtn2_TelecommunicationUseCode().setValue('P')
        phone2.getXtn3_TelecommunicationEquipmentType().setValue('M')

        baseSegment.getPid16_MaritalStatus().getCwe1_Identifier().setValue('S')
        baseSegment.getPid19_SSNNumberPatient().setValue('111-11-1111')
    }
    
}
