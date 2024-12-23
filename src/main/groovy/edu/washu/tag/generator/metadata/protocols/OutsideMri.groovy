package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.scanners.StRomanWestAchieva
import edu.washu.tag.generator.metadata.scanners.VandeventerRegionalHospitalAvanto
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.T1Weighted
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Weighted

class OutsideMri extends MriMinimalProtocol {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        final Localizer localizer = new Localizer() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [StRomanWestAchieva, VandeventerRegionalHospitalAvanto]
            }
        }
        final T1Weighted t1 = new T1Weighted() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [StRomanWestAchieva, VandeventerRegionalHospitalAvanto]
            }
        }
        final T2Weighted t2 = new T2Weighted() {
            @Override
            List<Class<? extends Equipment>> getCompatibleEquipment() {
                [StRomanWestAchieva, VandeventerRegionalHospitalAvanto]
            }
        }
        localizer.setAnatomicalPlane(AnatomicalPlane.TRANSVERSE)
        t1.setAnatomicalPlane(AnatomicalPlane.TRANSVERSE)
        t2.setAnatomicalPlane(AnatomicalPlane.TRANSVERSE)
        [localizer, t1, t2]
    }

}
