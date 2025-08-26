package edu.washu.tag.generator.hl7.v2.segment

class ObxManager {

    List<ObxGenerator> obxGenerators = []

    ObxManager() {

    }

    ObxManager(List<List<ObxGenerator>> generators) {
        generators.each {
            obxGenerators.addAll(it)
        }
    }

    void add(ObxGenerator obxGenerator) {
        add([obxGenerator])
    }

    void add(List<ObxGenerator> obxGenerators) {
        this.obxGenerators.addAll(obxGenerators)
    }

}
