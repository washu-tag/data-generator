package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.patient.MainId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Institutions {

    public static final List<Institution> knownInstitutions = []

    public static Institution centerForSpecializedRadiology
    public static Institution chestertonAdamsHospital
    public static Institution southGrandMammographySpecialty
    public static Institution stRomanWest
    public static Institution vandeventerRegionalHospital
    public static Institution hokkaidoRadiologyCenter
    public static Institution seoulInterventionalRadiologyClinic
    public static Institution westAthensGeneralHospital
    public static Institution koNullInstitution
    public static Institution rtNullInstitution

    private static List<Institution> overridesCache
    private static final Logger logger = LoggerFactory.getLogger(Institutions)

    static List<Institution> applyOverrides(List<Institution> overrides) {
        knownInstitutions.clear()
        overridesCache = overrides

        centerForSpecializedRadiology = register {
            institutionName = 'Center for Specialized Radiology'
            institutionAddress = '1 Compton Rd, San Francisco, CA 94102'
            assigningAuthority = MainId.assigningAuthority
        }
        chestertonAdamsHospital = register {
            institutionName = 'Chesterton-Adams Hospital'
            institutionAddress = '114 N Becker Rd, St. Louis, MO 63105'
            assigningAuthority = MainId.assigningAuthority
        }
        southGrandMammographySpecialty = register {
            institutionName = 'SGMS'
            assigningAuthority = MainId.assigningAuthority
        }
        stRomanWest = register {
            institutionName = 'St Roman West'
            institutionAddress = '4599 W Hammerton, St. Louis, MO 63130'
            assigningAuthority = MainId.assigningAuthority
        }
        vandeventerRegionalHospital = register {
            institutionName = 'Vandeventer Regional Hospital'
            institutionAddress = '4 Green Plaza, St. Louis, MO 63106'
            assigningAuthority = MainId.assigningAuthority
        }
        hokkaidoRadiologyCenter = register {
            institutionName = 'Hokkaido Radiology Center'
            institutionAddress = '487-1010, Kita 7-jonishi, Ashibetsu-shi, Hokkaido, Japan'
            commonNationality = Nationality.JAPANESE
        }
        seoulInterventionalRadiologyClinic = register {
            institutionName = 'Seoul Interventional Radiology Clinic'
            institutionAddress = '1484-9, Gayang 3(sam)-dong, Gangseo-gu, Seoul, Korea'
            commonNationality = Nationality.KOREAN
        }
        westAthensGeneralHospital = register {
            institutionName = 'West Athens General Hospital'
        }
        koNullInstitution = register {
            id = 'ko_null'
        }
        rtNullInstitution = register {
            id = 'rt_null'
        }

        final Set<Institution> unmatched = overridesCache.toSet() - knownInstitutions.toSet()
        if (!unmatched.isEmpty()) {
            logger.warn("Some institution overrides did not find a match: ${unmatched*.id}")
        }

        overridesCache = null
        knownInstitutions
    }

    private static Institution register(@DelegatesTo(value = Institution, strategy = Closure.DELEGATE_ONLY) Closure config) {
        final Institution institution = new Institution()
        institution.with(config)
        final Institution override = overridesCache.find { Institution match ->
            match.id == institution.id
        }
        final Institution effectiveInstitution = override ?: institution
        knownInstitutions << effectiveInstitution
        effectiveInstitution
    }

}
