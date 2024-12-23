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
import edu.washu.tag.generator.metadata.seriesTypes.nm.MyometrixResults
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

class GEInfinia implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian) : 90,
            (UID.ImplicitVRLittleEndian) : 10
    ])

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
        'INFINIA'
    }

    @Override
    String getStationName() {
        '93_GENM_INFI'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        'NM_MYOCARDIAL_PERFUSION'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['2.105.030.18']
    }

    @Override
    String getDeviceSerialNumber() {
        '16312'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        (seriesType instanceof MyometrixResults) ? 100000000 + ThreadLocalRandom.current().nextInt(900000000) : 1
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
