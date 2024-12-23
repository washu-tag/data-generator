package edu.washu.tag.generator.metadata.module

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Patient

interface PatientLevelModule {

    void apply(SpecificationParameters specificationParameters, Patient patient)

}
