package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.scanners.XaScanner
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.xa.GenericAngiography

class XRayAngiography extends Protocol {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        (1 .. 10).collect {
            new GenericAngiography()
        }
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        null
    }

    @Override
    String getStudyDescription(Equipment scanner, BodyPart bodyPart) {
        (scanner as XaScanner).studyDescription
    }

    @Override
    CodedTriplet getProcedureCode(BodyPart bodyPart) {
        new CodedTriplet(
                'ZIV67821',
                'UNKDEV',
                'XRAY ANGIO',
                'XRay Angiography'
        )
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'X-ray angiography study'
    }

}
