package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.RtNullInstitution
import edu.washu.tag.generator.util.RandomGenUtils

class AdacPinnacle3 implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian.toString()) : 80,
            (UID.ImplicitVRLittleEndian.toString()) : 20
    ])

    @Override
    Institution getInstitution() {
        new RtNullInstitution()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.ADAC
    }

    @Override
    String getModelName() {
        'Pinnacle3'
    }

    @Override
    String getStationName() {
        'RT-W-43b'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['9.6', '9.6']
    }

    @Override
    String getDeviceSerialNumber() {
        'W8M5FVMKYJHT'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        null
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        1
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
