package edu.washu.tag.generator.hl7.v2.model

enum TransportationMode {

    AMBULATORY (''), // this is an inference that could be wrong
    PORTABLE ('PORTABLE'),
    STRETCHER ('STRETCHE'),
    WHEELCHAIR ('WHEELCHA')

    String serialized2_4

    TransportationMode(String serialized2_4) {
        this.serialized2_4 = serialized2_4
    }

}