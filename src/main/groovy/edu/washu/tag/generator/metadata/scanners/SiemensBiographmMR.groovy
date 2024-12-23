package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.enums.RandomsCorrectionMethod
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.metadata.sequence.EnergyWindowRangeSequence
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtSeriesType
import edu.washu.tag.generator.metadata.seriesTypes.sr.PhoenixZIPReport
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class SiemensBiographmMR extends SiemensPetDevice {

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
    private static final EnumeratedDistribution<String> convolutionKernelRandomizer = RandomGenUtils.setupWeightedLottery([
            'XYZGAUSS3.00' : 60,
            'XYZGAUSS4.00' : 30,
            'XYZBOX' : 10
    ])

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
        'Biograph_mMR'
    }

    @Override
    String getStationName() {
        'PETMR-STAT-1'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        (seriesType instanceof PhoenixZIPReport) ? 99 : seriesIndex + 1
    }

    @Override
    String getProtocolName(Study study, Series series) {
        ThreadLocalRandom.current().nextBoolean() ? series.seriesDescription : series.seriesDescription + '_1'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['syngo MR B20P']
    }

    @Override
    String getDeviceSerialNumber() {
        'X1-43-9901'
    }

    @Override
    void resample() {
        currentImagingTransferSyntax = imagingTransferSyntaxRandomizer.sample()
        currentNonimagingTransferSyntax = nonimagingTransferSyntaxRandomizer.sample()
    }

    @Override
    String getTransferSyntaxUID(String sopClassUID) {
        switch (sopClassUID) {
            case UID.EnhancedSRStorage : // only supported non-imaging instance here for now
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
        null
    }

    @Override
    List<String> getConvolutionKernel() {
        [convolutionKernelRandomizer.sample()]
    }

    @Override
    String getReconstructionMethod() {
        'OSEM3D 2i8s'
    }

    @Override
    String getAxialAcceptance() {
        '60'
    }

    @Override
    EnergyWindowRangeSequence getEnergyWindowRangeSequence() {
        final EnergyWindowRangeSequence sequence = new EnergyWindowRangeSequence()
        sequence << new EnergyWindowRangeSequence.Item(lowerLimit : '430', upperLimit: '610')
        sequence
    }

    @Override
    boolean encodeRadiopharmaceutical() {
        true
    }

}
