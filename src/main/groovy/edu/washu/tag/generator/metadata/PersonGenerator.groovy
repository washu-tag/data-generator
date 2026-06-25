package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.enums.Nationality

import java.util.function.Function
import java.util.function.Supplier

class PersonGenerator<X> {

    private Supplier<String> idGenerator
    private boolean includeMdSuffix = false
    private boolean dropMiddle = false
    private Function<X, HierarchicDesignator> assigningAuthorityDerivation
    private final int numTotalPeople
    private final Function<X, Nationality> nationalityFunction

    PersonGenerator(int numTotalPeople, Function<X, Nationality> nationalityFunction) {
        this.numTotalPeople = numTotalPeople
        this.nationalityFunction = nationalityFunction
    }

    PersonGenerator<X> idGenerator(Supplier<String> idGenerator) {
        this.idGenerator = idGenerator
        this
    }

    PersonGenerator<X> includeMd() {
        includeMdSuffix = true
        this
    }

    PersonGenerator<X> dropMiddle() {
        dropMiddle = true
        this
    }

    PersonGenerator<X> inferAssigningAuthorityFrom(Function<X, HierarchicDesignator> function) {
        assigningAuthorityDerivation = function
        this
    }

    <K> PersonCache<K> cachePeople(List<X> personLinks, Function<X, K> keyExtractor) {
        new PersonCache<K>(personLinks.collectEntries { personLink ->
            final Nationality derivedNationality = nationalityFunction.apply(personLink)
            final HierarchicDesignator assigningAuthority = assigningAuthorityDerivation?.apply(personLink)

            [(keyExtractor.apply(personLink)) : (1 .. numTotalPeople).collect {
                final Person person = derivedNationality.generateRandomPerson()
                if (dropMiddle) {
                    person.middleName(null)
                }
                if (derivedNationality == Nationality.AMERICAN && includeMdSuffix) {
                    person.degree('M.D.')
                }
                if (idGenerator != null) {
                    person.setPersonIdentifier(idGenerator.get())
                }
                if (assigningAuthority != null) {
                    person.setAssigningAuthority(assigningAuthority)
                }
                person
            }]
        })
    }

}
