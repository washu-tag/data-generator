package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.scanners.KoreanInstitutionGEDiscoveryXR656
import edu.washu.tag.generator.metadata.seriesTypes.dx.DigitalXRayForPresentation

class KoreanInstitutionXRay extends SingleViewXRay {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new DigitalXRayForPresentation() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [KoreanInstitutionGEDiscoveryXR656]
            }
        }]
    }

    @Override
    boolean isApplicableFor(Patient patient) {
        patient.nationality == Nationality.KOREAN
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                '9414DX',
                '99LOC',
                'XR DIAG',
                'Diagnostic XRay'
        )
    }

    @Override
    boolean isXnatCompatible() {
        false
    }

}
