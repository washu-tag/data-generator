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

class GEOptimaMR450w implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian): 90,
            (UID.ImplicitVRLittleEndian): 10
    ])
    private static final List<Integer> seriesNumbers = [1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 601, 602, 1101]

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.GE
    }

    @Override
    String getModelName() {
        'Optima MR450w'
    }

    @Override
    String getStationName() {
        'GEMS1234'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['14', 'LX', 'MR Software release:14.0_M4_0620.a']
    }

    @Override
    String getDeviceSerialNumber() {
        '0000000291844MR2'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        readSeriesNumberFromIndexedList(seriesNumbers, seriesIndex)
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
