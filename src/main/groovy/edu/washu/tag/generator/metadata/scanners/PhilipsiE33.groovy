package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.util.RandomGenUtils

class PhilipsiE33 implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.JPEGLossless) : 50,
            (UID.JPEGBaseline8Bit): 30,
            (UID.ExplicitVRLittleEndian): 20
    ])

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
        'iE33'
    }

    @Override
    String getStationName() {
        'US_MOD-1'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        'Free Form'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['PMS5.1 Ultrasound iE33_4.0.0.12']
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        1
    }

    @Override
    String getDeviceSerialNumber() {
        '626735593'
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
