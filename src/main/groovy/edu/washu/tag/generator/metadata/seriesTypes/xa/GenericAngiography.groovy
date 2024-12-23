package edu.washu.tag.generator.metadata.seriesTypes.xa

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.XaScanner
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.scanners.PhilipsAlluraXper
import edu.washu.tag.generator.metadata.scanners.SiemensAxiomArtis

class GenericAngiography extends XaSeriesType {

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        (scanner as XaScanner).seriesDescription
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [PhilipsAlluraXper, SiemensAxiomArtis]
    }

    @Override
    XaImageType getXaImageType(XaScanner scanner) {
        scanner.imageType
    }

}
