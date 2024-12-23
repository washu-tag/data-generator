package edu.washu.tag.generator.metadata.module

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study

interface SeriesLevelModule<X extends Series> {

    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, X series)

}