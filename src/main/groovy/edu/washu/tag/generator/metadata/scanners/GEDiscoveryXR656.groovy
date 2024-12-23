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

import java.util.concurrent.ThreadLocalRandom

class GEDiscoveryXR656 implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian) : 50,
            (UID.ImplicitVRLittleEndian) : 30,
            (UID.ExplicitVRBigEndian) : 20
    ])

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.GE_HEALTHCARE
    }

    @Override
    String getModelName() {
        'Discovery XR656'
    }

    @Override
    String getStationName() {
        'CAH_XR_S_1'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        "${study.bodyPartExamined.dicomRepresentation}_1_VIEW"
    }

    @Override
    List<String> getSoftwareVersions() {
        ['dm_Platform_release-FW01_2-1985']
    }

    @Override
    String getDeviceSerialNumber() {
        '938814ZC9'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        ThreadLocalRandom.current().nextInt(10000)
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
