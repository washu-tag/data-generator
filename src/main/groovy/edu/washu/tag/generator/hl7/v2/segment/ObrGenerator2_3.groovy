package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.segment.OBR
import ca.uhn.hl7v2.util.DeepCopy
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.hl7.v2.model.TransportationMode
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.TimeUtils

class ObrGenerator2_3 extends ObrGenerator {

    /*
      Observations from real data to explain the choices made here:

      * OBR-4 only has the first two components
      * OBR-5 and OBR-6 are empty
      * OBR-7 has some sort of timestamp
      * OBR-11 is empty
      * OBR-13 has an all caps bit of background information
      * OBR-15 through OBR-20 are all empty
      * OBR-24 is empty
      * OBR-30 has the same string repeated in the first and second components. The string can be
        "AMBULATORY", "PORTABLE", "WHEELCHAIR", or "STRETCHER"
      * OBR-31 is usually a repeated string like "Pain\S\Pain" (\S\ is the default escape code for the component delimiter)
      * OBR-32 usually looks like 123^LAST^FIRST^MIDDLE^^^M.D., but there are several other possibilities. I have seen "M.D"
        in OBR-32.4 instead of middle name, or I have seen M.D. in OBR-32.5 and OBR-32.7. Other seen values from examples
        for OBR-32.7 are "MD" or "RADIOLOGIST".
      * OBR-33 is just a literal ~
      * OBR-34 looks like ^LAST^FIRST^^^^, which has of note both no ID, and 7 forced components
      * OBR-35 looks like ^UnkPERSON^UnkPERSON^^^^ in every case I have seen. I have modified the placeholder slightly,
        but it seems constant.

     */
    @Override
    void generateSegment(RadiologyReport radReport, OBR baseSegment) {
        final Study study = radReport.study
        final CodedTriplet procedureCode = ProcedureCode.lookup(study.procedureCodeId).codedTriplet

        baseSegment.getObr1_SetIDOBR().setValue('1')
        DeepCopy.copy(radReport.placerOrderNumber, baseSegment.getObr2_PlacerOrderNumber())
        DeepCopy.copy(radReport.fillerOrderNumber, baseSegment.getObr3_FillerOrderNumber())

        final CWE universalServiceIdentifier = baseSegment.getObr4_UniversalServiceIdentifier()
        universalServiceIdentifier.getCwe1_Identifier().setValue(procedureCode.codeValue)
        universalServiceIdentifier.getCwe2_Text().setValue(procedureCode.codeMeaning)

        baseSegment.getObr7_ObservationDateTime().setValue(
            TimeUtils.toHl7(
                study.studyDateTime() // TODO: what time to use?
            )
        )

        // TODO: OBR-13

        baseSegment.getObr22_ResultsRptStatusChngDateTime().setValue(TimeUtils.toHl7(radReport.reportDateTime))

        baseSegment.getObr25_ResultStatus().setValue(radReport.orcStatus.currentText)

        // TODO: OBR-27

        final String transportationMode = (radReport.transportationMode ?: TransportationMode.AMBULATORY).name()
        final Terser terser = new Terser(baseSegment.getMessage()) // using Terser to set components that shouldn't exist
        terser.set('/.OBR-30-1', transportationMode)
        terser.set('/.OBR-30-2', transportationMode)

        baseSegment.getObr31_ReasonForStudy(0).getCwe1_Identifier().setValue(
            "${radReport.reasonForStudy}^${radReport.reasonForStudy}" // ^ should get escaped as \S\
        )

        final NameEncoder nameEncoder = new NameEncoder(radReport, baseSegment)
        nameEncoder.encodePrincipalResultInterpreter(radReport.principalInterpreter)
        baseSegment.insertRepetition(33, 0)
        baseSegment.insertRepetition(33, 1)

        final Person tech = radReport.technician
        if (tech != null) {
            nameEncoder.encodeTechnician(tech)
        }
        new Terser(baseSegment.message).set('/.OBR-34-1', '') // TODO: revisit if we add non-malformed OBR names

        final Person transcriptionist = OrcGenerator2_3.enteredBy
        terser.set('/.OBR-35-2', transcriptionist.familyNameAlphabetic) // using terser to avoid capitalization coercion
        terser.set('/.OBR-35-3', transcriptionist.givenNameAlphabetic)
    }

}
