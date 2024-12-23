package edu.washu.tag.generator.metadata.scanners

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.metadata.enums.CollimatorType
import edu.washu.tag.generator.metadata.enums.DecayCorrection
import edu.washu.tag.generator.metadata.enums.FieldOfViewShape
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetUnit
import edu.washu.tag.generator.metadata.enums.RandomsCorrectionMethod
import edu.washu.tag.generator.metadata.enums.TypeOfDetectorMotion
import edu.washu.tag.generator.metadata.sequence.EnergyWindowRangeSequence
import edu.washu.tag.generator.metadata.series.PtSeries
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtSeriesType

trait PetScanner {

    @JsonIgnore
    DecayCorrection getDecayCorrection() {
        DecayCorrection.START
    }

    @JsonIgnore
    CollimatorType getCollimatorType() {
        CollimatorType.NONE
    }

    @JsonIgnore
    abstract List<PetImageCorrection> getNonAttenuatedCorrections()

    @JsonIgnore
    abstract List<PetImageCorrection> getAttenuatedCorrections()

    @JsonIgnore
    abstract PetUnit getUnits(PtSeries series)

    @JsonIgnore
    abstract String getScatterCorrectionMethod(PtSeries series)

    @JsonIgnore
    abstract List<String> getAxialMash()

    @JsonIgnore
    abstract List<String> getConvolutionKernel()

    @JsonIgnore
    abstract RandomsCorrectionMethod getRandomsCorrectionMethod()

    @JsonIgnore
    abstract String getAttenuationCorrectionMethod(PtSeriesType seriesType)

    @JsonIgnore
    abstract String getReconstructionMethod()

    @JsonIgnore
    abstract String getAxialAcceptance()

    @JsonIgnore
    abstract EnergyWindowRangeSequence getEnergyWindowRangeSequence()

    @JsonIgnore
    abstract boolean encodeRadiopharmaceutical()

    @JsonIgnore
    abstract String getReconstructionDiameter()

    @JsonIgnore
    abstract FieldOfViewShape getFieldOfViewShape()

    @JsonIgnore
    abstract List<String> getFieldOfViewDimensions()

    @JsonIgnore
    abstract TypeOfDetectorMotion getTypeOfDetectorMotion()

    @JsonIgnore
    abstract boolean encodeRoute()

    @JsonIgnore
    abstract boolean encodeRadiopharmaceuticalStartDateTime()

}