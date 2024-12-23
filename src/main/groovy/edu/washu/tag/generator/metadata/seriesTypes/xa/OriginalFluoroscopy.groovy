package edu.washu.tag.generator.metadata.seriesTypes.xa

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.scanners.XaScanner
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.scanners.PhilipsAlluraXper

class OriginalFluoroscopy extends XaSeriesType {

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        'Fluoroscopy'
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [PhilipsAlluraXper]
    }

    @Override
    XaImageType getXaImageType(XaScanner scanner) {
        new XaImageType().addValue('SINGLE A')
    }

}
