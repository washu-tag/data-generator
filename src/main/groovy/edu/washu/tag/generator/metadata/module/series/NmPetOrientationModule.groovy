package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.contextGroups.PatientEquipmentRelationship
import edu.washu.tag.generator.metadata.enums.PatientOrientation
import edu.washu.tag.generator.metadata.enums.contextGroups.PatientOrientationModifier
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.series.SupportsNmPetPatientOrientation

class NmPetOrientationModule implements SeriesLevelModule<Series> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, Series series) {
        final SupportsNmPetPatientOrientation castedSeries = series as SupportsNmPetPatientOrientation
        castedSeries.setPatientOrientationSeqValue(PatientOrientation.RECUMBENT)
        castedSeries.setPatientOrientationModifierSeqValue(PatientOrientationModifier.SUPINE)
        castedSeries.setPatientGantryRelationshipSeqValue(PatientEquipmentRelationship.HEADFIRST)
    }

}
