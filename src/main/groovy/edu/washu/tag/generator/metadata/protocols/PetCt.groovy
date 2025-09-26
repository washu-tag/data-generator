package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.GeneralizedProcedure
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.ct.CtWithAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.ct.PatientProtocol
import edu.washu.tag.generator.metadata.seriesTypes.ct.Topogram
import edu.washu.tag.generator.metadata.seriesTypes.ot.SiemensProprietaryCtFusion
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtNonAttenuationCorrected
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtWithAttenuationCorrection
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

import static edu.washu.tag.generator.util.StringReplacements.BODYPART

class PetCt extends PetStudy {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            ("PET^PETCT_AC_NAC_${BODYPART}".toString()) : 30,
            ("PETCT^${BODYPART}".toString()) : 20,
            ("PET^01_PETCT_${BODYPART} (Adult)".toString()) : 20,
            'PET/CT^ADULT' : 15,
            'PET-CT STUDY' : 10
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Topogram(),
                new SiemensProprietaryCtFusion(),
                new CtWithAttenuationCorrection(),
                new PtWithAttenuationCorrection(),
                new PtNonAttenuationCorrected(),
                new PatientProtocol()
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [
                BodyPart.ABDOMEN,
                BodyPart.BRAIN,
                BodyPart.HEAD,
                BodyPart.THORAX,
                BodyPart.WHOLEBODY
        ]
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        switch (bodyPart) {
            case BodyPart.ABDOMEN -> ProcedureCode.lookup('petct abdomen') // this doesnt match very well, but I don't see anything better
            case [BodyPart.BRAIN, BodyPart.HEAD] -> ProcedureCode.lookup('petct brain')
            case BodyPart.THORAX -> ProcedureCode.lookup('petct thorax') // this doesnt match very well, but I don't see anything better
            case BodyPart.WHOLEBODY -> ProcedureCode.lookup('petct wholebody')
            default -> throw new UnsupportedOperationException("Unsupported body part ${bodyPart}")
        }
    }

    @Override
    GeneralizedProcedure getGeneralizedProcedure() {
        GeneralizedProcedure.PET
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        randomizeWithBodyPart(randomizer, study.bodyPartExamined)
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        "${bodyPart.dicomRepresentation.toLowerCase()} PET-CT"
    }
}
