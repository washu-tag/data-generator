package edu.washu.tag.generator.metadata.enums

import edu.washu.tag.generator.util.FileIOUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

enum Nationality {

    AMERICAN {
        final EnumeratedDistribution<String> surnameRandomizer = setupNameRandomizer('surnames.txt')
        final EnumeratedDistribution<String> maleGivenNameRandomizer = setupNameRandomizer('male_given_names.txt')
        final EnumeratedDistribution<String> femaleGivenNameRandomizer = setupNameRandomizer('female_given_names.txt')
        final EnumeratedDistribution<String> suffixRandomizer = RandomGenUtils.setupWeightedLottery([
                (null) : 980,
                'Sr.' : 5,
                'Jr.' : 5,
                'III' : 4,
                'M.D.' : 4,
                'Ph.D.' : 2
        ])
        final EnumeratedDistribution<String> prefixRandomizer = RandomGenUtils.setupWeightedLottery([
                (null) : 980,
                'Capt.' : 5,
                'Sgt.' : 4,
                'Col.' : 4,
                'Lt. Col.' : 4,
                'Adm.' : 3
        ])

        EnumeratedDistribution<String> setupNameRandomizer(String fileName) {
            RandomGenUtils.setupWeightedLottery(FileIOUtils.readResource(fileName).split('\n').collectEntries { line ->
                final List<String> nameLine = line.split('\\|')
                [(nameLine[0]) : Integer.parseInt(nameLine[1])]
            })
        }

        @Override
        Person generateRandomPerson(Sex sex) {
            final EnumeratedDistribution<String> givenNameRandomizer = (ensureSexProvided(sex) == Sex.MALE) ? maleGivenNameRandomizer : femaleGivenNameRandomizer
            new Person().
                    familyName(surnameRandomizer.sample()).
                    givenName(givenNameRandomizer.sample()).
                    middleName(givenNameRandomizer.sample()).
                    suffix(suffixRandomizer.sample()).
                    prefix(prefixRandomizer.sample())
        }
    },
    GREEK {
        final List<String> greekSurnames = readNameList('greek_surnames.txt')
        final List<String> greekMaleGivenNames = readNameList('greek_male_given_names.txt')
        final List<String> greekFemaleGivenNames = readNameList('greek_female_given_names.txt')

        @Override
        Person generateRandomPerson(Sex sex) {
            final List<String> familyName = RandomGenUtils.randomListEntry(greekSurnames).split('\\|')
            final List<String> givenName = RandomGenUtils.randomListEntry(ensureSexProvided(sex) == Sex.MALE ? greekMaleGivenNames : greekFemaleGivenNames).split('\\|')

            new Person(requiredCharacterSets: ['', 'ISO 2022 IR 126']).
                familyNameTransliterated(familyName[0], familyName[1]).
                givenNameTransliterated(givenName[0], givenName[1])
        }
    },
    JAPANESE {
        final List<String> japaneseSurnames = readNameList('japanese_surnames.txt')
        final List<String> japaneseMaleGivenNames = readNameList('japanese_male_given_names.txt')
        final List<String> japaneseFemaleGivenNames = readNameList('japanese_female_given_names.txt')

        @Override
        Person generateRandomPerson(Sex sex) {
            randomizeMultigroupName(sex, japaneseMaleGivenNames, japaneseFemaleGivenNames, japaneseSurnames, ['', 'ISO 2022 IR 87'])
        }
    },
    KOREAN {
        final List<String> koreanSurnames = readNameList('korean_surnames.txt')
        final List<String> koreanMaleGivenNames = readNameList('korean_male_given_names.txt')
        final List<String> koreanFemaleGivenNames = readNameList('korean_female_given_names.txt')

        @Override
        Person generateRandomPerson(Sex sex) {
            randomizeMultigroupName(sex, koreanMaleGivenNames, koreanFemaleGivenNames, koreanSurnames, ['', 'ISO 2022 IR 149'])
        }
    }

    abstract Person generateRandomPerson(Sex sex = null)

    protected static Sex ensureSexProvided(Sex sex) {
        if (sex != null) {
            sex
        } else {
            ThreadLocalRandom.current().nextBoolean() ? Sex.MALE : Sex.FEMALE
        }
    }

    protected Person randomizeMultigroupName(Sex sex, List<String> maleGivenNames, List<String> femaleGivenNames, List<String> surnames, List<String> characterSets) {
        final List<String> givenNameRepresentations = RandomGenUtils.randomListEntry(ensureSexProvided(sex) == Sex.MALE ? maleGivenNames : femaleGivenNames).split('\\|')
        final List<String> surnameRepresentations = RandomGenUtils.randomListEntry(surnames).split('\\|')
        new Person(requiredCharacterSets: characterSets).
                familyName(surnameRepresentations[0], surnameRepresentations[1], surnameRepresentations[2]).
                givenName(givenNameRepresentations[0], givenNameRepresentations[1], givenNameRepresentations[2])
    }

    protected List<String> readNameList(String fileName) {
        FileIOUtils.readResource(fileName).split('\n')
    }

}