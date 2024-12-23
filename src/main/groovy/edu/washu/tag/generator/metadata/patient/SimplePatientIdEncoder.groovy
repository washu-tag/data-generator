package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.util.SequentialIdGenerator

class SimplePatientIdEncoder implements PatientIdEncoder {

    Class<? extends PatientId> patientIdClass
    SequentialIdGenerator idGenerator

    SimplePatientIdEncoder(Class<? extends PatientId> patientIdClass) {
        this.patientIdClass = patientIdClass
        idGenerator = new SequentialIdGenerator()
        idGenerator.setPrefix(initPatientId().getAssigningAuthority().getNamespaceId())
    }

    @Override
    PatientId nextPatientId() {
        final PatientId patientId = initPatientId()
        patientId.setIdNumber(idGenerator.get())
        patientId
    }

    private PatientId initPatientId() {
        patientIdClass.getDeclaredConstructor().newInstance()
    }

}
