package edu.washu.tag.generator.hl7.v2.model

import ca.uhn.hl7v2.model.Segment
import ca.uhn.hl7v2.model.v281.datatype.XCN
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.metadata.Person

class DoctorEncoder2_7 extends DoctorEncoder {

    public static final String HOSP = 'HOSP'

    @Override
    void encode(Person person, XCN element) {
        person.toXcn(element, true, true)
        if (!includeDegree()) {
            element.getXcn7_DegreeEgMD().setValue(null)
        }
        if (!includeAssigningAuthority()) {
            element.getXcn9_AssigningAuthority().getHd1_NamespaceID().setValue(null)
        }
        if (includeIdentifierTypeCode()) {
            element.getXcn13_IdentifierTypeCode().setValue(HOSP)
        }
    }

    @Override
    protected void encodePostCheck(List<Person> people, Segment segment, int fieldId, boolean malform) {
        final Closure<Void> setViaTerser = { int rep, int pieceId, String value ->
            Terser.set(
                segment,
                fieldId,
                rep,
                malform ? pieceId : 1,
                malform ? 1 : pieceId,
                value
            )
        }

        people.eachWithIndex { person, i ->
            setViaTerser(i, 1, person.personIdentifier.toUpperCase())
            setViaTerser(i, 2, person.familyNameAlphabetic.toUpperCase())
            setViaTerser(i, 3, person.givenNameAlphabetic.toUpperCase())
            setViaTerser(i, 4, person.middleInitial()?.toUpperCase()) // TODO: this doesn't have to be an initial
            if (includeDegree()) {
                setViaTerser(i, 7, person.degree)
            }
            if (includeAssigningAuthority() && person.getAssigningAuthority() != null) {
                setViaTerser(i, 9, person.getAssigningAuthority().getNamespaceId())
            }
            if (includeIdentifierTypeCode() && person.getAssigningAuthority() != null) {
                setViaTerser(i, 13, HOSP)
            }
        }
    }

    @Override
    boolean includeDegree() {
        false
    }

    @Override
    boolean includeAssigningAuthority() {
        true
    }

    @Override
    boolean includeIdentifierTypeCode() {
        true
    }

}
