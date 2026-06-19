package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.*
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.ct.CtWithAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.ct.PatientProtocol
import edu.washu.tag.generator.metadata.seriesTypes.ct.Topogram

class SimpleCt extends PetCt {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Topogram(),
                new CtWithAttenuationCorrection(),
                new PatientProtocol()
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [
                BodyPart.BRAIN,
                BodyPart.HEAD
        ]
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('ct head')
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.CT
    }


    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} CT"
    }

}
