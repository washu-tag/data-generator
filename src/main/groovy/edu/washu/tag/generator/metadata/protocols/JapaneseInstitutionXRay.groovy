package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.scanners.JapaneseInstitutionGEDiscoveryXR656
import edu.washu.tag.generator.metadata.seriesTypes.dx.DigitalXRayForPresentation

class JapaneseInstitutionXRay extends SingleViewXRay {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new DigitalXRayForPresentation() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [JapaneseInstitutionGEDiscoveryXR656]
            }
        }]
    }

    @Override
    boolean isApplicableFor(Patient patient) {
        patient.nationality == Nationality.JAPANESE
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('outside transfer xray')
    }

    @Override
    boolean isXnatCompatible() {
        false
    }

}
