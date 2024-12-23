package edu.washu.tag.generator.metadata.enums

enum AcquisitionTerminationCondition {

    CNTS,
    DENS,
    RDD,
    MANU,
    OVFL,
    TIME,
    TRIG

    String getDicomRepresentation() {
        name()
    }

}