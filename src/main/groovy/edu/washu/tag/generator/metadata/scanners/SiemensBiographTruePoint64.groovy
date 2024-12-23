package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.enums.RandomsCorrectionMethod
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.metadata.sequence.EnergyWindowRangeSequence
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtSeriesType
import edu.washu.tag.generator.util.RandomGenUtils

class SiemensBiographTruePoint64 extends SiemensPetDevice implements CtScanner {

    private String currentImagingTransferSyntax
    private String currentNonimagingTransferSyntax

    private static final EnumeratedDistribution<String> imagingTransferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian) : 100,
            (UID.JPEGLossless) : 50,
            (UID.JPEGBaseline8Bit) : 50,
            (UID.ImplicitVRLittleEndian) : 10,
            (UID.JPEGExtended12Bit) : 10
    ])
    private static final EnumeratedDistribution<String> nonimagingTransferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian) : 90,
            (UID.ImplicitVRLittleEndian) : 10
    ])

    private static final List<Integer> seriesNumbers = [1, 1, 2, 4, 5, 501]

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.SIEMENS
    }

    @Override
    String getModelName() {
        'Biograph64_TruePoint'
    }

    @Override
    String getStationName() {
        'PETCT_001'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        readSeriesNumberFromIndexedList(seriesNumbers, seriesIndex)
    }

    @Override
    ImageType getTopogramImageType() {
        getCtImageType(true).addValue('CT_SOM5 TOP')
    }

    @Override
    ImageType getAttenuationCorrectedCtImageType() {
        getCtImageType(false).addValue('CT_SOM5 SPI')
    }

    @Override
    String getProtocolName(Study study, Series series) {
        study.studyDescription.split('\\^').last()
    }

    @Override
    List<String> getSoftwareVersions() {
        ['VG10A']
    }

    @Override
    String getDeviceSerialNumber() {
        '34566'
    }

    @Override
    void resample() {
        currentImagingTransferSyntax = imagingTransferSyntaxRandomizer.sample()
        currentNonimagingTransferSyntax = nonimagingTransferSyntaxRandomizer.sample()
    }

    @Override
    String getTransferSyntaxUID(String sopClassUID) {
        switch (sopClassUID) {
            case UID.PrivateSiemensCSANonImageStorage : // only supported non-imaging instance here for now
                return currentNonimagingTransferSyntax
            default :
                return currentImagingTransferSyntax
        }
    }

    @Override
    RandomsCorrectionMethod getRandomsCorrectionMethod() {
        RandomsCorrectionMethod.DLYD
    }

    @Override
    String getAttenuationCorrectionMethod(PtSeriesType seriesType) {
        (seriesType.isAttenuationCorrected()) ? 'CT-derived mu-map' : null
    }

    @Override
    List<String> getConvolutionKernel() {
        ['XYZ G5.00']
    }

    @Override
    String getReconstructionMethod() {
        'PSF 4i21s'
    }

    @Override
    String getAxialAcceptance() {
        '38'
    }

    @Override
    EnergyWindowRangeSequence getEnergyWindowRangeSequence() {
        final EnergyWindowRangeSequence sequence = new EnergyWindowRangeSequence()
        sequence << new EnergyWindowRangeSequence.Item(lowerLimit : '435', upperLimit: '650')
        sequence
    }

    @Override
    boolean encodeRadiopharmaceutical() {
        false
    }

}
