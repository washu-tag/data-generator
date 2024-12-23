package edu.washu.tag.generator.metadata

class ImageType {

    boolean original = true
    boolean primary = true
    List<String> otherValues = []

    ImageType derived() {
        setOriginal(false)
        this
    }

    ImageType secondary() {
        setPrimary(false)
        this
    }

    ImageType addValue(String value) {
        otherValues << value
        this
    }

    List<String> resolve() {
        [original ? 'ORIGINAL' : 'DERIVED', primary ? 'PRIMARY' : 'SECONDARY'] + otherValues
    }

}
