package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator

class SimplePatientIdEncoder implements PatientIdEncoder {

    Class<? extends PatientId> patientIdClass
    SequentialIdGenerator idGenerator

    SimplePatientIdEncoder(Class<? extends PatientId> patientIdClass, Integer baseId = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)) {
        this.patientIdClass = patientIdClass
        idGenerator = new SequentialIdGenerator().currentId(baseId)
        final HierarchicDesignator hd = initPatientId().getAssigningAuthority()
        if (hd != null) {
            idGenerator.setPrefix(hd.getNamespaceId())
        }
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
