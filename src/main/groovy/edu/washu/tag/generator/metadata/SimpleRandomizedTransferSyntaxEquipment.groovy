package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.commons.math3.distribution.EnumeratedDistribution

trait SimpleRandomizedTransferSyntaxEquipment implements Equipment {

    @JsonIgnore
    String currentTransferSyntax

    @JsonIgnore
    abstract EnumeratedDistribution<String> getTransferSyntaxRandomizer()

    @Override
    void resample() {
        currentTransferSyntax = transferSyntaxRandomizer.sample()
    }

    @Override
    String getTransferSyntaxUID(String sopClassUID) {
        currentTransferSyntax
    }

}