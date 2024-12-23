package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.CountsSource
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue1
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.scanners.PetScanner
import edu.washu.tag.generator.metadata.series.PtSeries
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtSeriesType

/**
 * Implementation of this module is tied to and powered by the conformance statement for the Biograph TruePoint 64 and Biograph mMR modalities. If other PET modalities are added, this class will need to be modified.
 * @see edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64
 * @see edu.washu.tag.generator.metadata.scanners.SiemensBiographmMR
 */
class PetSeriesModule implements SeriesLevelModule<PtSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, PtSeries series) {
        final PtSeriesType seriesType = series.seriesType as PtSeriesType
        final PetScanner scanner = equipment as PetScanner
        series.setUnits(scanner.getUnits(series))
        series.setSeriesTypeValue1(seriesType.seriesTypeValue1)
        series.setSeriesTypeValue2(seriesType.seriesTypeValue2)
        series.setCountsSource(CountsSource.EMISSION)
        if (series.seriesTypeValue1 == PetSeriesTypeValue1.GATED) {
            series.setNumberOfRRIntervals(1)
            series.setNumberOfTimeSlots(1) // TODO: this should be reconsidered if supporting more than 1 instance per series
        } else if (series.seriesTypeValue1 == PetSeriesTypeValue1.DYNAMIC) {
            series.setNumberOfTimeSlices(1) // TODO: this should be reconsidered if supporting more than 1 instance per series
        }
        series.setNumberOfSlices(1) // TODO: this should be reconsidered if supporting more than 1 instance per series
        series.setImageCorrections(seriesType.getImageCorrections(series))
        series.setRandomsCorrectionMethod(scanner.randomsCorrectionMethod)
        series.setAttenuationCorrectionMethod(scanner.getAttenuationCorrectionMethod(seriesType))
        series.setScatterCorrectionMethod(scanner.getScatterCorrectionMethod(series))
        series.setDecayCorrection(scanner.decayCorrection)
        series.setReconstructionDiameter(scanner.reconstructionDiameter)
        series.setConvolutionKernel(scanner.convolutionKernel)
        series.setReconstructionMethod(scanner.reconstructionMethod)
        series.setFieldOfViewShape(scanner.fieldOfViewShape)
        series.setFieldOfViewDimensions(scanner.fieldOfViewDimensions)
        series.setTypeOfDetectorMotion(scanner.typeOfDetectorMotion)
        series.setCollimatorType(scanner.collimatorType)
        series.setAxialAcceptance(scanner.axialAcceptance)
        series.setAxialMash(scanner.axialMash)
        series.setEnergyWindowRangeSequence(scanner.energyWindowRangeSequence)
    }

}
