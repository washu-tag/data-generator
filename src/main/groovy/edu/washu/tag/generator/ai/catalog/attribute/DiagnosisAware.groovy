package edu.washu.tag.generator.ai.catalog.attribute

import com.fasterxml.jackson.annotation.JsonIgnore

trait DiagnosisAware {

    @JsonIgnore
    private DiagnosisCodeDesignator designator = DiagnosisCodeDesignator.randomize()

    @JsonIgnore
    abstract List<DiagnosisCode> resolveDiagnoses()

    DiagnosisCodeDesignator getDesignator() {
        designator
    }

    void setDesignator(DiagnosisCodeDesignator designator) {
        this.designator = designator
    }

}