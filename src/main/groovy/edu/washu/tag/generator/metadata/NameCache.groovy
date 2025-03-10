package edu.washu.tag.generator.metadata


import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.institutions.AbcUmbrellaInstitution
import edu.washu.tag.generator.metadata.institutions.CenterForSpecializedRadiology
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.metadata.institutions.HokkaidoRadiologyCenter
import edu.washu.tag.generator.metadata.institutions.KoNullInstitution
import edu.washu.tag.generator.metadata.institutions.RtNullInstitution
import edu.washu.tag.generator.metadata.institutions.SeoulInterventionalRadiologyClinic
import edu.washu.tag.generator.metadata.institutions.SouthGrandMammographySpecialty
import edu.washu.tag.generator.metadata.institutions.StRomanWest
import edu.washu.tag.generator.metadata.institutions.VandeventerRegionalHospital
import edu.washu.tag.generator.metadata.institutions.WestAthensGeneralHospital
import edu.washu.tag.generator.util.SequentialIdGenerator

import java.util.function.Function

class NameCache {

    private static final int NUM_OPERATORS = 10
    private static final int NUM_HOSPITAL_PHYSICIANS = 15
    private static final int NUM_NATIONAL_PHYSICIANS = 200
    private static final List<Class<? extends Institution>> allInstitutions = [
        CenterForSpecializedRadiology,
        ChestertonAdamsHospital,
        HokkaidoRadiologyCenter,
        KoNullInstitution,
        RtNullInstitution,
        SeoulInterventionalRadiologyClinic,
        SouthGrandMammographySpecialty,
        StRomanWest,
        VandeventerRegionalHospital,
        WestAthensGeneralHospital
    ] // TODO: wouldn't have to hardcode this if we add org.reflections dependency, but is that worth it?

    private static final Function<Class<? extends Institution>, Nationality> INSTITUTION_NATIONALITY_MAPPER =
            { Class<? extends Institution> institutionClass ->
                institutionClass.getDeclaredConstructor().newInstance().commonNationality()
            }

    private static NameCache instance

    PersonCache<Class<? extends Institution>> operators
    PersonCache<Class<? extends Institution>> hospitalPhysicians
    PersonCache<Nationality> generalPhysicians

    static NameCache initInstance() {
        instance = new NameCache(
            operators: new PersonGenerator<>(NUM_OPERATORS, INSTITUTION_NATIONALITY_MAPPER)
                .dropMiddle()
                .idGenerator(new SequentialIdGenerator().prefix('T'))
                .cachePeople(allInstitutions),
            hospitalPhysicians: new PersonGenerator<>(NUM_HOSPITAL_PHYSICIANS, INSTITUTION_NATIONALITY_MAPPER)
                .includeMd()
                .idGenerator(new SequentialIdGenerator().prefix('D'))
                .inferAssigningAuthorityFrom({ Class<? extends Institution> institution ->
                    institution.getDeclaredConstructor().newInstance().assigningAuthority()
                }).cachePeople(allInstitutions),
            generalPhysicians: new PersonGenerator<>(NUM_NATIONAL_PHYSICIANS, Function.identity())
                .includeMd()
                .idGenerator(new SequentialIdGenerator().prefix('D'))
                .cachePeople(Nationality.values() as List<Nationality>)
        )
        instance
    }

    static void cache(NameCache nameCache) {
        instance = nameCache
    }

    static List<Person> selectOperators(Institution institution, int numOperators) {
        instance.operators.selectPeople(institution.class, numOperators)
    }

    static Person selectOperator(Institution institution) {
        instance.operators.selectPerson(institution.class)
    }

    static List<Person> selectPhysicians(Institution institution, int numPhysicians) {
        instance.hospitalPhysicians.selectPeople(institution.class, numPhysicians)
    }

    static Person selectPhysician(Institution institution) {
        instance.hospitalPhysicians.selectPerson(institution.class)
    }

    static List<Person> selectPhysicians(Nationality nationality, int numPhysicians) {
        instance.generalPhysicians.selectPeople(nationality, numPhysicians)
    }

    static Person selectPhysician(Nationality nationality) {
        instance.generalPhysicians.selectPerson(nationality)
    }

}
