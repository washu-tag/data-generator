package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.util.RandomGenUtils

class PersonCache<X> {

    private Map<X, List<Person>> personCache

    PersonCache(Map<X, List<Person>> personCache) {
        this.personCache = personCache
    }

    PersonCache() {

    }

    List<Person> selectPeople(X personLink, int numPeople) {
        RandomGenUtils.randomSubset(personCache[personLink], numPeople)
    }

    Person selectPerson(X personLink) {
        selectPeople(personLink, 1)[0]
    }

}
