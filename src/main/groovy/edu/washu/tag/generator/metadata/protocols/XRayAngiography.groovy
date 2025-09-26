package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
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
    String getStudyDescription(Equipment scanner, Study study) {
        (scanner as XaScanner).studyDescription
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('xray angio')
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.IR
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'X-ray angiography study'
    }

}
