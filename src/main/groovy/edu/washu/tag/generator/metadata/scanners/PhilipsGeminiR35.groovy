package edu.washu.tag.generator.metadata.scanners

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.FieldOfViewShape
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.enums.PetImageCorrection
import edu.washu.tag.generator.metadata.enums.PetUnit
import edu.washu.tag.generator.metadata.enums.RandomsCorrectionMethod
import edu.washu.tag.generator.metadata.enums.TypeOfDetectorMotion
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.metadata.sequence.EnergyWindowRangeSequence
import edu.washu.tag.generator.metadata.series.PtSeries
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtSeriesType
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class PhilipsGeminiR35 implements Equipment, CtScanner, PetScanner {

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.PHILIPS
    }

    @Override
    String getModelName() {
        'Gemini'
    }

    @Override
    String getStationName() {
        'PETCT_002'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['3.5']
    }

    @Override
    String getDeviceSerialNumber() {
        null
    }

    @Override
    String getProtocolName(Study study, Series series) {
        null
    }

    @Override
    String getTransferSyntaxUID(String sopClassUID) {
        UID.ExplicitVRLittleEndian
    }

    @Override
    ImageType getTopogramImageType() {
        getCtImageType(true)
    }

    @Override
    ImageType getAttenuationCorrectedCtImageType() {
        getCtImageType(false).addValue('HELIX')
    }

    @Override
    String getScatterCorrectionMethod(PtSeries series) {
        (series.imageCorrections.contains(PetImageCorrection.SCAT)) ? 'NONUNIFORM' : 'NONE'
    }

    @Override
    List<PetImageCorrection> getNonAttenuatedCorrections() {
        [
                PetImageCorrection.DECY,
                PetImageCorrection.RADL,
                PetImageCorrection.DTIM,
                PetImageCorrection.RAN,
                PetImageCorrection.NORM
        ]
    }

    @Override
    List<PetImageCorrection> getAttenuatedCorrections() {
        [
                PetImageCorrection.DECY,
                PetImageCorrection.RADL,
                PetImageCorrection.ATTN,
                PetImageCorrection.SCAT,
                PetImageCorrection.DTIM,
                PetImageCorrection.RAN,
                PetImageCorrection.NORM
        ]
    }

    @Override
    PetUnit getUnits(PtSeries series) {
        RandomGenUtils.weightedCoinFlip(80) ? PetUnit.BQML : PetUnit.CNTS
    }

    @Override
    List<String> getAxialMash() {
        null
    }

    @Override
    List<String> getConvolutionKernel() {
        null
    }

    @Override
    RandomsCorrectionMethod getRandomsCorrectionMethod() {
        RandomsCorrectionMethod.DLYD
    }

    @Override
    String getAttenuationCorrectionMethod(PtSeriesType seriesType) {
        (seriesType.isAttenuationCorrected()) ? 'CTAC-SG' : 'NONE'
    }

    @Override
    String getReconstructionMethod() {
        ThreadLocalRandom.current().nextBoolean() ? '3D-RAMLA' : 'RAMLA'
    }

    @Override
    String getAxialAcceptance() {
        null
    }

    @Override
    EnergyWindowRangeSequence getEnergyWindowRangeSequence() {
        null
    }

    @Override
    boolean encodeRadiopharmaceutical() {
        true
    }

    @Override
    String getReconstructionDiameter() {
        '576'
    }

    @Override
    FieldOfViewShape getFieldOfViewShape() {
        FieldOfViewShape.CYLINDRICAL_RING
    }

    @Override
    List<String> getFieldOfViewDimensions() {
        ['903', '180']
    }

    @Override
    TypeOfDetectorMotion getTypeOfDetectorMotion() {
        TypeOfDetectorMotion.NONE
    }

    @Override
    boolean encodeRoute() {
        true
    }

    @Override
    boolean encodeRadiopharmaceuticalStartDateTime() {
        true
    }

}
