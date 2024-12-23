package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.us.UsSeriesType

class Ultrasound extends Protocol {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [new UsSeriesType()]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        null // not usually encoded in the DICOM data for ultrasounds
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        null // not usually encoded in the DICOM data for ultrasounds
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV00931',
                'UNKDEV',
                'ULTRASOUND DIAG',
                'Ultrasound'
        )
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'ultrasound'
    }

}
