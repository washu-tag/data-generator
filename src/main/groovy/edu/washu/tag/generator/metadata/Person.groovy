package edu.washu.tag.generator.metadata

import ca.uhn.hl7v2.model.DataTypeException
import ca.uhn.hl7v2.model.v281.datatype.XCN
import ca.uhn.hl7v2.model.v281.datatype.XPN
import com.fasterxml.jackson.annotation.JsonInclude
import edu.washu.tag.generator.hl7.v2.model.CodedValue
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.PersonName
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.function.Function

import static org.dcm4che3.data.PersonName.Component.*
import static org.dcm4che3.data.PersonName.Group.*

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@JsonInclude(JsonInclude.Include.NON_NULL)
class Person {

    private static final EnumeratedDistribution<Boolean> capitalizationRandomizer = RandomGenUtils.setupWeightedLottery([(false): 99, (true): 1])
    private static final EnumeratedDistribution<Closure<String>> middleInitialRandomizer = RandomGenUtils.setupWeightedLottery([
            ({ name -> null }) : 990,
            ({ name -> name.charAt(0) }) : 5,
            ({ name -> "${name.charAt(0)}." }) : 3,
            ({ name -> name }) : 2
    ]) as EnumeratedDistribution<Closure>
    private static final EnumeratedDistribution<Closure<String>> titleRandomizer = RandomGenUtils.setupWeightedLottery([
            ({ suffix -> suffix }) : 90,
            ({ suffix -> suffix.replace('.', '') }) : 7,
            ({ suffix -> null }) : 3
    ]) as EnumeratedDistribution<Closure>

    String familyNameAlphabetic
    String givenNameAlphabetic
    String middleNameAlphabetic
    String prefixAlphabetic
    String suffixAlphabetic
    String familyNameTransliterated
    String givenNameTransliterated
    String middleNameTransliterated
    String prefixTransliterated
    String suffixTransliterated
    String familyNameIdeographic
    String givenNameIdeographic
    String familyNamePhonetic
    String givenNamePhonetic
    List<String> requiredCharacterSets = []
    
    // HL7
    String degree
    String nameTypeCode
    String nameRepresentationCode
    CodedValue nameContext
    String nameValidityRange
    String nameAssemblyOrder
    String effectiveDate
    String expirationDate
    String professionalSuffix
    String calledBy
    String personIdentifier
    CodedValue sourceTable
    HierarchicDesignator assigningAuthority
    String identifierCheckDigit
    String checkDigitScheme
    String identifierTypeCode
    HierarchicDesignator assigningFacility
    CodedValue assigningJurisdiction
    CodedValue assigningAgencyOrDepartment
    String securityCheck
    String securityCheckScheme

    Person familyName(String alphabetic) {
        familyNameAlphabetic = alphabetic
        this
    }

    Person givenName(String alphabetic) {
        givenNameAlphabetic = alphabetic
        this
    }

    Person middleName(String alphabetic) {
        middleNameAlphabetic = alphabetic
        this
    }

    Person suffix(String alphabetic) {
        suffixAlphabetic = alphabetic
        this
    }

    Person prefix(String alphabetic) {
        prefixAlphabetic = alphabetic
        this
    }

    Person familyName(String alphabetic, String ideographic, String phonetic) {
        familyNameAlphabetic = alphabetic
        familyNameIdeographic = ideographic
        familyNamePhonetic = phonetic
        this
    }

    Person givenName(String alphabetic, String ideographic, String phonetic) {
        givenNameAlphabetic = alphabetic
        givenNameIdeographic = ideographic
        givenNamePhonetic = phonetic
        this
    }

    Person familyNameTransliterated(String alphabetic, String transliterated) {
        setFamilyNameAlphabetic(alphabetic)
        setFamilyNameTransliterated(transliterated)
        this
    }

    Person givenNameTransliterated(String alphabetic, String transliterated) {
        setGivenNameAlphabetic(alphabetic)
        setGivenNameTransliterated(transliterated)
        this
    }

    String serializeToDicom(boolean allowNonlatin = false) {
        final PersonName personName = new PersonName()
        final Closure<String> coercionFunction = capitalizationRandomizer.sample() ? { String name -> name.toUpperCase() } : { String name -> name }

        personName.set(Alphabetic, FamilyName, coercionFunction(transliterate(familyNameAlphabetic, familyNameTransliterated, allowNonlatin)))
        personName.set(Alphabetic, GivenName, coercionFunction(transliterate(givenNameAlphabetic, givenNameTransliterated, allowNonlatin)))
        if (middleNameAlphabetic != null) {
            final String middleRepresentation = middleInitialRandomizer.sample()(transliterate(middleNameAlphabetic, middleNameTransliterated, allowNonlatin))
            if (middleRepresentation != null) {
                personName.set(Alphabetic, MiddleName, coercionFunction(middleRepresentation))
            }
        }
        if (suffixAlphabetic != null) {
            final String suffixRepresentation = titleRandomizer.sample()(transliterate(suffixAlphabetic, suffixTransliterated, allowNonlatin))
            if (suffixRepresentation != null) {
                personName.set(Alphabetic, NameSuffix, coercionFunction(suffixRepresentation))
            }
        }
        if (prefixAlphabetic != null) {
            final String prefixRepresentation = titleRandomizer.sample()(transliterate(prefixAlphabetic, prefixTransliterated, allowNonlatin))
            if (prefixRepresentation != null) {
                personName.set(Alphabetic, NamePrefix, coercionFunction(prefixRepresentation))
            }
        }
        if (allowNonlatin) {
            if (familyNameIdeographic != null) {
                personName.set(Ideographic, FamilyName, familyNameIdeographic)
            }
            if (givenNameIdeographic != null) {
                personName.set(Ideographic, GivenName, givenNameIdeographic)
            }
            if (familyNamePhonetic != null) {
                personName.set(Phonetic, FamilyName, familyNamePhonetic)
            }
            if (givenNamePhonetic != null) {
                personName.set(Phonetic, GivenName, givenNamePhonetic)
            }
        }

        personName.toString()
    }

    String serializeToInitials() {
        "${givenNameAlphabetic.charAt(0)}${middleNameAlphabetic?.charAt(0) ?: ''}${familyNameAlphabetic.charAt(0)}"
    }

    String middleInitial() {
        middleNameAlphabetic != null ? "${middleNameAlphabetic.charAt(0)}." : null
    }

    XPN toXpn(XPN emptyDataStore, boolean coerceToUpper = false) throws DataTypeException {
        final Function<String, String> transformer = coerceToUpper ? String::toUpperCase : Function.identity()

        emptyDataStore.getXpn1_FamilyName().getFn1_Surname().setValue(transformer.apply(familyNameAlphabetic))
        emptyDataStore.getXpn2_GivenName().setValue(transformer.apply(givenNameAlphabetic))
        if (middleNameAlphabetic != null) {
            emptyDataStore.getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue(transformer.apply(middleNameAlphabetic))
        }
        emptyDataStore.getXpn4_SuffixEgJRorIII().setValue(suffixAlphabetic)
        emptyDataStore.getXpn5_PrefixEgDR().setValue(prefixAlphabetic)
        emptyDataStore.getXpn6_DegreeEgMD().setValue(degree)
        emptyDataStore.getXpn7_NameTypeCode().setValue(nameTypeCode)
        emptyDataStore.getXpn8_NameRepresentationCode().setValue(nameRepresentationCode)
        if (nameContext != null) {
            nameContext.toCwe(emptyDataStore.getXpn9_NameContext())
        }
        emptyDataStore.getXpn10_NameValidityRange().setValue(nameValidityRange)
        emptyDataStore.getXpn11_NameAssemblyOrder().setValue(nameAssemblyOrder)
        if (effectiveDate != null) {
            emptyDataStore.getXpn12_EffectiveDate().setValue(effectiveDate)
        }
        if (expirationDate != null) {
            emptyDataStore.getXpn13_ExpirationDate().setValue(expirationDate)
        }
        emptyDataStore.getXpn14_ProfessionalSuffix().setValue(professionalSuffix)
        emptyDataStore.getXpn15_CalledBy().setValue(calledBy)
        emptyDataStore
    }

    XCN toXcn(XCN emptyDataStore, boolean coerceToUpper = false, boolean truncateMiddle = false) {
        final Function<String, String> transformer = coerceToUpper ? String::toUpperCase : Function.identity()
        emptyDataStore.getXcn1_PersonIdentifier().setValue(personIdentifier)
        emptyDataStore.getXcn2_FamilyName().getFn1_Surname().setValue(transformer.apply(familyNameAlphabetic))
        emptyDataStore.getXcn3_GivenName().setValue(transformer.apply(givenNameAlphabetic))
        if (middleNameAlphabetic != null) {
            emptyDataStore.getXcn4_SecondAndFurtherGivenNamesOrInitialsThereof().setValue(
                    transformer.apply(truncateMiddle ? middleInitial() : middleNameAlphabetic)
            )
        }
        emptyDataStore.getXcn5_SuffixEgJRorIII().setValue(suffixAlphabetic)
        emptyDataStore.getXcn6_PrefixEgDR().setValue(prefixAlphabetic)
        emptyDataStore.getXcn7_DegreeEgMD().setValue(degree)
        if (sourceTable != null) {
            sourceTable.toCwe(emptyDataStore.getXcn8_SourceTable())
        }
        if (assigningAuthority != null) {
            assigningAuthority.toHd(emptyDataStore.getXcn9_AssigningAuthority())
        }
        emptyDataStore.getXcn10_NameTypeCode().setValue(nameTypeCode)
        emptyDataStore.getXcn11_IdentifierCheckDigit().setValue(identifierCheckDigit)
        emptyDataStore.getXcn12_CheckDigitScheme().setValue(checkDigitScheme)
        emptyDataStore.getXcn13_IdentifierTypeCode().setValue(identifierTypeCode)
        if (assigningFacility != null) {
            assigningFacility.toHd(emptyDataStore.getXcn14_AssigningFacility())
        }
        emptyDataStore.getXcn15_NameRepresentationCode().setValue(nameRepresentationCode)
        if (nameContext != null) {
            nameContext.toCwe(emptyDataStore.getXcn16_NameContext())
        }
        emptyDataStore.getXcn17_NameValidityRange().setValue(nameValidityRange)
        emptyDataStore.getXcn18_NameAssemblyOrder().setValue(nameAssemblyOrder)
        if (effectiveDate != null) {
            emptyDataStore.getXcn19_EffectiveDate().setValue(effectiveDate)
        }
        if (effectiveDate != null) {
            emptyDataStore.getXcn20_ExpirationDate().setValue(expirationDate)
        }
        emptyDataStore.getXcn21_ProfessionalSuffix().setValue(professionalSuffix)
        if (assigningJurisdiction != null) {
            assigningJurisdiction.toCwe(emptyDataStore.getXcn22_AssigningJurisdiction())
        }
        if (assigningAgencyOrDepartment != null) {
            assigningAgencyOrDepartment.toCwe(emptyDataStore.getXcn23_AssigningAgencyOrDepartment())
        }
        emptyDataStore.getXcn24_SecurityCheck().setValue(securityCheck)
        emptyDataStore.getXcn25_SecurityCheckScheme().setValue(securityCheckScheme)
        emptyDataStore
    }

    String formatLastFirstMiddle(boolean includeSpace) {
        "${familyNameAlphabetic},${includeSpace ? ' ' : ''}${givenNameAlphabetic} ${middleInitial()}"
    }

    String formatFirstLast() {
        "${givenNameAlphabetic} ${familyNameAlphabetic}"
    }

    private static String transliterate(String alphabetic, String transliterated, boolean allowNonlatin) {
        (!allowNonlatin && transliterated != null) ? transliterated : alphabetic
    }

}
