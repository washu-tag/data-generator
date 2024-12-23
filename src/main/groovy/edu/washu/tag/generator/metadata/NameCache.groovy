package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator

import java.util.function.Function
import java.util.function.Supplier

class NameCache {

    private static final int NUM_OPERATORS = 10
    private static final int NUM_HOSPITAL_PHYSICIANS = 15
    private static final int NUM_NATIONAL_PHYSICIANS = 200

    private static final Function<Class<? extends Institution>, Nationality> INSTITUTION_NATIONALITY_MAPPER =
            { Class<? extends Institution> institutionClass ->
                institutionClass.getDeclaredConstructor().newInstance().commonNationality()
            }
    private static final PersonGenerator<Class<? extends Institution>> operators = new PersonGenerator<>(
            NUM_OPERATORS, INSTITUTION_NATIONALITY_MAPPER
    ).dropMiddle().idGenerator(new SequentialIdGenerator().prefix('T'))

    private static final PersonGenerator<Class<? extends Institution>> hospitalPhysicians =
            new PersonGenerator<>(NUM_HOSPITAL_PHYSICIANS, INSTITUTION_NATIONALITY_MAPPER)
                .includeMd()
                .idGenerator(new SequentialIdGenerator().prefix('D'))
                .inferAssigningAuthorityFrom({ Class<? extends Institution> institution ->
                    institution.getDeclaredConstructor().newInstance().assigningAuthority()
                })

    private static final PersonGenerator<Nationality> generalPhysicians = new PersonGenerator<>(
            NUM_NATIONAL_PHYSICIANS, Function.identity()
    ).includeMd().idGenerator(new SequentialIdGenerator().prefix('D'))

    static List<Person> selectOperators(Institution institution, int numOperators) {
        operators.selectPeople(institution.class, numOperators)
    }

    static Person selectOperator(Institution institution) {
        operators.selectPerson(institution.class)
    }

    static List<Person> selectPhysicians(Institution institution, int numPhysicians) {
        hospitalPhysicians.selectPeople(institution.class, numPhysicians)
    }

    static Person selectPhysician(Institution institution) {
        hospitalPhysicians.selectPerson(institution.class)
    }

    static List<Person> selectPhysicians(Nationality nationality, int numPhysicians) {
        generalPhysicians.selectPeople(nationality, numPhysicians)
    }

    static Person selectPhysician(Nationality nationality) {
        generalPhysicians.selectPerson(nationality)
    }

    private static class PersonGenerator<X> {
        private Supplier<String> idGenerator
        private boolean includeMdSuffix = false
        private boolean dropMiddle = false
        private Function<X, HierarchicDesignator> assigningAuthorityDerivation
        private final int numTotalPeople
        private final Function<X, Nationality> nationalityFunction
        private final Map<X, List<Person>> personCache = [:]

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

        List<Person> selectPeople(X personLink, int numPeople) {
            if (!personCache.containsKey(personLink)) {
                final Nationality derivedNationality = nationalityFunction.apply(personLink)
                final HierarchicDesignator assigningAuthority = assigningAuthorityDerivation?.apply(personLink)

                personCache.put(
                        personLink,
                        (1 .. numTotalPeople).collect {
                            final Person person = derivedNationality.generateRandomPerson()
                            if (dropMiddle) {
                                person.middleName(null)
                            }
                            if (derivedNationality == Nationality.AMERICAN && includeMdSuffix) {
                                person.suffix('M.D.')
                            }
                            if (idGenerator != null) {
                                person.setPersonIdentifier(idGenerator.get())
                            }
                            if (assigningAuthority != null) {
                                person.setAssigningAuthority(assigningAuthority)
                            }
                            person
                        }
                )
            }
            RandomGenUtils.randomSubset(personCache[personLink], numPeople)
        }

        Person selectPerson(X personLink) {
            selectPeople(personLink, 1)[0]
        }
    }

}
