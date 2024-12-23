package edu.washu.tag.generator.metadata.module.patient

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.metadata.module.PatientLevelModule
import edu.washu.tag.generator.util.RandomGenUtils

import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

class PatientModule implements PatientLevelModule {

    public static final LocalDate leftDobEndpoint = LocalDate.of(1920, 1, 1) // TODO: don't do this
    public static final LocalDate rightDobEndpoint = LocalDate.of(2002, 1, 1) // TODO: don't do this

    // Patient Name, Patient ID, and Ethnic Group are handled outside
    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient) {
        patient.setDateOfBirth(RandomGenUtils.randomDate(leftDobEndpoint, rightDobEndpoint))
        patient.setSex(ThreadLocalRandom.current().nextBoolean() ? Sex.MALE : Sex.FEMALE)
        patient.setTimeOfBirth(RandomGenUtils.randomTime())
    }

}
