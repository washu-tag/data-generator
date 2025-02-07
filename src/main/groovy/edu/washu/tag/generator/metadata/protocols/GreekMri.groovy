package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.scanners.GreekSiemensAvanto
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.T1Weighted
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Weighted

class GreekMri extends MriMinimalProtocol {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        final Localizer localizer = new Localizer() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [GreekSiemensAvanto]
            }
        }
        final T1Weighted t1 = new T1Weighted() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [GreekSiemensAvanto]
            }
        }
        final T2Weighted t2 = new T2Weighted() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [GreekSiemensAvanto]
            }
        }
        localizer.setAnatomicalPlane(AnatomicalPlane.TRANSVERSE)
        t1.setAnatomicalPlane(AnatomicalPlane.TRANSVERSE)
        t2.setAnatomicalPlane(AnatomicalPlane.TRANSVERSE)
        [localizer, t1, t2]
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('greek outside brain mri')
    }

    @Override
    boolean isXnatCompatible() {
        false
    }

    @Override
    boolean isApplicableFor(Patient patient) {
        patient.nationality == Nationality.GREEK
    }

}
