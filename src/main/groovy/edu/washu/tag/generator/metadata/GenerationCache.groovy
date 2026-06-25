package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.util.SequentialIdGenerator

import java.util.function.Function

class GenerationCache {

    private static final int NUM_OPERATORS = 10
    private static final int NUM_HOSPITAL_PHYSICIANS = 15
    private static final int NUM_NATIONAL_PHYSICIANS = 200

    private static final Function<Institution, Nationality> INSTITUTION_NATIONALITY_MAPPER =
            { Institution institution ->
                institution.commonNationality
            }

    private static GenerationCache instance

    List<Institution> institutions
    PersonCache<String> operators
    PersonCache<String> hospitalPhysicians
    PersonCache<Nationality> generalPhysicians

    static GenerationCache initInstance(SpecificationParameters specificationParameters) {
        final List<Institution> allInstitutions = Institutions.applyOverrides(specificationParameters.institutionOverrides)
        instance = new GenerationCache(
            operators: new PersonGenerator<>(NUM_OPERATORS, INSTITUTION_NATIONALITY_MAPPER)
                .dropMiddle()
                .idGenerator(new SequentialIdGenerator().prefix('T'))
                .cachePeople(allInstitutions, { it.id }),
            hospitalPhysicians: new PersonGenerator<>(NUM_HOSPITAL_PHYSICIANS, INSTITUTION_NATIONALITY_MAPPER)
                .includeMd()
                .idGenerator(new SequentialIdGenerator().prefix('D'))
                .inferAssigningAuthorityFrom({ Institution institution ->
                    institution.assigningAuthority
                }).cachePeople(allInstitutions, { it.id }),
            generalPhysicians: new PersonGenerator<>(NUM_NATIONAL_PHYSICIANS, Function.identity())
                .includeMd()
                .idGenerator(new SequentialIdGenerator().prefix('D'))
                .cachePeople(Nationality.values() as List<Nationality>, Function.identity()),
            institutions: allInstitutions
        )
        instance
    }

    static List<Person> selectOperators(Institution institution, int numOperators) {
        instance.operators.selectPeople(institution.id, numOperators)
    }

    static Person selectOperator(Institution institution) {
        instance.operators.selectPerson(institution.id)
    }

    static List<Person> selectPhysicians(Institution institution, int numPhysicians) {
        instance.hospitalPhysicians.selectPeople(institution.id, numPhysicians)
    }

    static Person selectPhysician(Institution institution) {
        instance.hospitalPhysicians.selectPerson(institution.id)
    }

    static List<Person> selectPhysicians(Nationality nationality, int numPhysicians) {
        instance.generalPhysicians.selectPeople(nationality, numPhysicians)
    }

    static Person selectPhysician(Nationality nationality) {
        instance.generalPhysicians.selectPerson(nationality)
    }

    void cache() {
        instance = this
        Institutions.applyOverrides(institutions)
    }

}
