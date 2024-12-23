package edu.washu.tag.generator.metadata.module

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study

interface StudyLevelModule {

    void apply(SpecificationParameters specificationParameters, Patient patient, Study study)

}
