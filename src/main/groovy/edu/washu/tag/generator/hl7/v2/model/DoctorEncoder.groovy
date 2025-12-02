package edu.washu.tag.generator.hl7.v2.model

import ca.uhn.hl7v2.model.Segment
import ca.uhn.hl7v2.model.v281.datatype.XCN
import edu.washu.tag.generator.metadata.Person

abstract class DoctorEncoder {

    abstract void encode(Person person, XCN element)

    protected abstract void encodePostCheck(List<Person> people, Segment segment, int fieldId, boolean malform)

    abstract boolean includeDegree()

    abstract boolean includeAssigningAuthority()

    abstract boolean includeIdentifierTypeCode()

    void encode(List<Person> people, Segment segment, int fieldId, boolean malform) {
        if (!malform) {
            throw new UnsupportedOperationException('There are currently only examples for malformed data')
        }
        encodePostCheck(people, segment, fieldId, malform)
    }

}