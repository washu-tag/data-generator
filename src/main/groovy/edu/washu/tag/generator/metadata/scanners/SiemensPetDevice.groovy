package edu.washu.tag.generator.metadata.scanners

import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.enums.FieldOfViewShape
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetUnit
import edu.washu.tag.generator.metadata.enums.TypeOfDetectorMotion
import edu.washu.tag.generator.metadata.series.PtSeries
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtSeriesType

abstract class SiemensPetDevice implements Equipment, PetScanner {

    @Override
    String getScatterCorrectionMethod(PtSeries series) {
        if ([PetImageCorrection.SCAT, PetImageCorrection._2SCAT, PetImageCorrection._3SCAT].any { it in series.imageCorrections }) {
            'Model-based'
        } else {
            null
        }
    }

    @Override
    List<String> getAxialMash() {
        ['5', '6']
    }

    @Override
    List<PetImageCorrection> getNonAttenuatedCorrections() {
        [
                PetImageCorrection.NORM,
                PetImageCorrection.DTIM,
                PetImageCorrection.DECY,
                PetImageCorrection.FLEN,
                PetImageCorrection.RANSM,
                PetImageCorrection.XYSM,
                PetImageCorrection.ZSM
        ]
    }

    @Override
    List<PetImageCorrection> getAttenuatedCorrections() {
        [
                PetImageCorrection.NORM,
                PetImageCorrection.DTIM,
                PetImageCorrection.ATTN,
                PetImageCorrection._3SCAT,
                PetImageCorrection.DECY,
                PetImageCorrection.FLEN,
                PetImageCorrection.RANSM,
                PetImageCorrection.XYSM,
                PetImageCorrection.ZSM
        ]
    }

    @Override
    PetUnit getUnits(PtSeries series) {
        (series.seriesType as PtSeriesType).isAttenuationCorrected() ? PetUnit.BQML : PetUnit.PROPCPS
    }

    @Override
    String getReconstructionDiameter() {
        null
    }

    @Override
    FieldOfViewShape getFieldOfViewShape() {
        null
    }

    @Override
    List<String> getFieldOfViewDimensions() {
        null
    }

    @Override
    TypeOfDetectorMotion getTypeOfDetectorMotion() {
        null
    }

    @Override
    boolean encodeRoute() {
        false
    }

    @Override
    boolean encodeRadiopharmaceuticalStartDateTime() {
        false
    }

}
