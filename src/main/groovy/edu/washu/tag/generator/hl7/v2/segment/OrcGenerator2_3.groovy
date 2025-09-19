package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.segment.ORC
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.TimeUtils

import java.time.LocalDateTime

class OrcGenerator2_3 extends OrcGenerator {

    /*
      Observations from real data to explain the choices made here:

      * Overall, this segment is much more sparse than in 2.7
      * PID-2 through PID-8 are empty, unlike in 2.7
      * ORC-10 is usually an odd value that looks like a placeholder for unknown. I'm going to use a similar but not
        identical placeholder: ^UnkPERSON^UnkPERSON^^^^
      * There is nothing beyond ORC-12

     */

    public static final Person enteredBy = new Person().givenName('UnkPERSON').familyName('UnkPERSON')

    @Override
    void generateSegment(RadiologyReport radReport, ORC baseSegment) {
        baseSegment.getOrc1_OrderControl().setValue('RE') // 'Observations/Performed Service to follow'

        final LocalDateTime reportDateTime = radReport.reportDateTime
        baseSegment.getOrc9_DateTimeOfTransaction().setValue(TimeUtils.toHl7(reportDateTime))

        enteredBy.toXcn(baseSegment.getOrc10_EnteredBy(0), false)

        radReport.getDoctorEncoder().encode(radReport.orderingProvider, baseSegment.getOrc12_OrderingProvider(0))
    }

}
