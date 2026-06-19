package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.SequentialIdGenerator

import java.util.function.Function

class SimplePatientIdGenerator implements PatientIdGenerator {

    SequentialIdGenerator idGenerator
    Function<Study, PatientId> patientIdCustomizer

    SimplePatientIdGenerator(int offset, Function<Study, PatientId> patientIdFunction) {
        idGenerator = new SequentialIdGenerator().currentId(offset)
        patientIdCustomizer = patientIdFunction
    }

    @Override
    String nextPatientId() {
        idGenerator.get()
    }

    @Override
    PatientId materializeIdForStudy(Study study, String id) {
        final PatientId patientId = patientIdCustomizer.apply(study)
        patientId.setIdNumber(id)
        patientId
    }

}
