package edu.washu.tag.generator.metadata.module

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Series

interface EquipmentLevelModule {

    void apply(SpecificationParameters specificationParameters, Equipment equipment, Series series)

}