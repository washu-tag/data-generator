package edu.washu.tag.generator.metadata.series

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.AcquisitionStartCondition
import edu.washu.tag.generator.metadata.enums.AcquisitionTerminationCondition
import edu.washu.tag.generator.metadata.enums.CollimatorType
import edu.washu.tag.generator.metadata.enums.CountsSource
import edu.washu.tag.generator.metadata.enums.DecayCorrection
import edu.washu.tag.generator.metadata.enums.FieldOfViewShape
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue1
import edu.washu.tag.generator.metadata.enums.PetSeriesTypeValue2
import edu.washu.tag.generator.metadata.enums.PetUnit
import edu.washu.tag.generator.metadata.enums.RandomsCorrectionMethod
import edu.washu.tag.generator.metadata.enums.ReprojectionMethod
import edu.washu.tag.generator.metadata.enums.ScanProgressionDirection
import edu.washu.tag.generator.metadata.enums.SecondaryCountsType
import edu.washu.tag.generator.metadata.enums.SuvType
import edu.washu.tag.generator.metadata.enums.TriggerSourceOrType
import edu.washu.tag.generator.metadata.enums.TypeOfDetectorMotion
import edu.washu.tag.generator.metadata.enums.YN
import edu.washu.tag.generator.metadata.sequence.EnergyWindowRangeSequence
import edu.washu.tag.generator.metadata.sequence.InterventionDrugInformationSequence
import edu.washu.tag.generator.metadata.sequence.RadiopharmaceuticalInformationSequence

class PtSeries extends Series implements SupportsNmPetPatientOrientation {

    // PET Series Module attributes
    PetUnit units
    SuvType suvType
    CountsSource countsSource
    PetSeriesTypeValue1 seriesTypeValue1
    PetSeriesTypeValue2 seriesTypeValue2
    ReprojectionMethod reprojectionMethod
    int numberOfRRIntervals
    int numberOfTimeSlots
    int numberOfTimeSlices
    int numberOfSlices
    List<PetImageCorrection> imageCorrections
    RandomsCorrectionMethod randomsCorrectionMethod
    String attenuationCorrectionMethod
    String scatterCorrectionMethod
    DecayCorrection decayCorrection
    String reconstructionDiameter
    List<String> convolutionKernel
    String reconstructionMethod
    String detectorLinesOfResponseUsed
    AcquisitionStartCondition acquisitionStartCondition
    String acquisitionStartConditionData
    AcquisitionTerminationCondition acquisitionTerminationCondition
    String acquisitionTerminationConditionData
    FieldOfViewShape fieldOfViewShape
    List<String> fieldOfViewDimensions
    String gantryDetectorTilt
    String gantryDetectorSlew
    TypeOfDetectorMotion typeOfDetectorMotion
    CollimatorType collimatorType
    String collimatorGridName
    String axialAcceptance
    List<String> axialMash
    String transverseMash
    List<String> detectorElementSize
    String coincidenceWindowWidth
    EnergyWindowRangeSequence energyWindowRangeSequence
    List<SecondaryCountsType> secondaryCountsType
    ScanProgressionDirection scanProgressionDirection

    // PET Isotope Module attributes
    RadiopharmaceuticalInformationSequence radiopharmaceuticalInformationSequence
    InterventionDrugInformationSequence interventionDrugInformationSequence

    // PET Multi-gated Acquisition Module attributes
    YN beatRejectionFlag
    TriggerSourceOrType triggerSourceOrType
    String pvcRejection
    String skipBeats
    String heartRate
    String cardiacFramingType

    @Override
    void encode(Attributes attributes) {
        super.encode(attributes)
        attributes.setString(Tag.Units, VR.CS, units.dicomRepresentation)
        setIfNonnull(attributes, Tag.SUVType, VR.CS, suvType?.dicomRepresentation)
        attributes.setString(Tag.CountsSource, VR.CS, countsSource.dicomRepresentation)
        setIfNonempty(attributes, Tag.SeriesType, VR.CS, [seriesTypeValue1.dicomRepresentation, seriesTypeValue2.dicomRepresentation])
        if (seriesTypeValue2 == PetSeriesTypeValue2.REPROJECTION) {
            attributes.setString(Tag.ReprojectionMethod, VR.CS, reprojectionMethod?.dicomRepresentation)
        }
        if (seriesTypeValue1 == PetSeriesTypeValue1.GATED) {
            attributes.setInt(Tag.NumberOfRRIntervals, VR.US, numberOfRRIntervals)
            attributes.setInt(Tag.NumberOfTimeSlots, VR.US, numberOfTimeSlots)
        }
        else if (seriesTypeValue1 == PetSeriesTypeValue1.DYNAMIC) {
            attributes.setInt(Tag.NumberOfTimeSlices, VR.US, numberOfTimeSlices)
        }
        attributes.setInt(Tag.NumberOfSlices, VR.US, numberOfSlices)
        setIfNonempty(attributes, Tag.CorrectedImage, VR.CS, serializeImageCorrections())
        setIfNonnull(attributes, Tag.RandomsCorrectionMethod, VR.CS, randomsCorrectionMethod?.dicomRepresentation)
        setIfNonnull(attributes, Tag.AttenuationCorrectionMethod, VR.LO, attenuationCorrectionMethod)
        setIfNonnull(attributes, Tag.ScatterCorrectionMethod, VR.LO, scatterCorrectionMethod)
        attributes.setString(Tag.DecayCorrection, VR.CS, decayCorrection.dicomRepresentation)
        setIfNonnull(attributes, Tag.ReconstructionDiameter, VR.DS, reconstructionDiameter)
        setIfNonempty(attributes, Tag.ConvolutionKernel, VR.SH, convolutionKernel)
        setIfNonnull(attributes, Tag.ReconstructionMethod, VR.LO, reconstructionMethod)
        setIfNonnull(attributes, Tag.DetectorLinesOfResponseUsed, VR.LO, detectorLinesOfResponseUsed)
        setIfNonnull(attributes, Tag.AcquisitionStartCondition, VR.CS, acquisitionStartCondition?.dicomRepresentation)
        setIfNonnull(attributes, Tag.AcquisitionStartConditionData, VR.IS, acquisitionStartConditionData)
        setIfNonnull(attributes, Tag.AcquisitionTerminationCondition, VR.CS, acquisitionTerminationCondition?.dicomRepresentation)
        setIfNonnull(attributes, Tag.AcquisitionTerminationConditionData, VR.IS, acquisitionTerminationConditionData)
        setIfNonnull(attributes, Tag.FieldOfViewShape, VR.CS, fieldOfViewShape?.dicomRepresentation)
        setIfNonempty(attributes, Tag.FieldOfViewDimensions, VR.IS, fieldOfViewDimensions)
        setIfNonnull(attributes, Tag.GantryDetectorTilt, VR.DS, gantryDetectorTilt)
        setIfNonnull(attributes, Tag.GantryDetectorSlew, VR.DS, gantryDetectorSlew)
        setIfNonnull(attributes, Tag.TypeOfDetectorMotion, VR.CS, typeOfDetectorMotion?.dicomRepresentation)
        attributes.setString(Tag.CollimatorType, VR.CS, collimatorType?.dicomRepresentation)
        setIfNonnull(attributes, Tag.CollimatorGridName, VR.SH, collimatorGridName)
        setIfNonnull(attributes, Tag.AxialAcceptance, VR.DS, axialAcceptance)
        setIfNonempty(attributes, Tag.AxialMash, VR.IS, axialMash)
        setIfNonnull(attributes, Tag.TransverseMash, VR.IS, transverseMash)
        setIfNonempty(attributes, Tag.DetectorElementSize, VR.DS, detectorElementSize)
        setIfNonnull(attributes, Tag.CoincidenceWindowWidth, VR.DS, coincidenceWindowWidth)
        if (energyWindowRangeSequence != null) {
            energyWindowRangeSequence.addToAttributes(attributes)
        }
        setIfNonempty(attributes, Tag.SecondaryCountsType, VR.CS, secondaryCountsType?.collect { it.dicomRepresentation })
        setIfNonnull(attributes, Tag.ScanProgressionDirection, VR.CS, scanProgressionDirection?.dicomRepresentation)

        (radiopharmaceuticalInformationSequence ?: new RadiopharmaceuticalInformationSequence()).addToAttributes(attributes)
        if (interventionDrugInformationSequence != null) {
            interventionDrugInformationSequence.addToAttributes(attributes)
        }

        if (seriesTypeValue1 == PetSeriesTypeValue1.GATED) {
            attributes.setString(Tag.BeatRejectionFlag, VR.CS, beatRejectionFlag?.dicomRepresentation)
            setIfNonnull(attributes, Tag.TriggerSourceOrType, VR.LO, triggerSourceOrType?.dicomRepresentation)
            setIfNonnull(attributes, Tag.PVCRejection, VR.LO, pvcRejection)
            setIfNonnull(attributes, Tag.SkipBeats, VR.IS, skipBeats)
            setIfNonnull(attributes, Tag.HeartRate, VR.IS, heartRate)
            setIfNonnull(attributes, Tag.CardiacFramingType, VR.LO, cardiacFramingType)
        }

        encodePatientOrientation(attributes)
    }

    private List<String> serializeImageCorrections() {
        if (imageCorrections != null && !imageCorrections.isEmpty()) {
            imageCorrections.collect { correction ->
                correction.dicomRepresentation
            }
        } else {
            ['']
        }
    }

}
