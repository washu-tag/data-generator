package edu.washu.tag.generator.metadata.module.instance

import org.dcm4che3.util.UIDUtils
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.InstanceLevelModule

class SopCommonModule implements InstanceLevelModule {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, Series series, Instance instance, int instanceIndex) {
        instance.setSopClassUid(series.seriesType.sopClassUid)
        instance.setSopInstanceUid(UIDUtils.createUID())
        // TODO: Instance Creation Date and Time
        instance.setInstanceNumber(String.valueOf(1 + instanceIndex)) // TODO: some modalities get more fancy
        // TODO: Private Data Element Characteristics Sequence
    }

}
