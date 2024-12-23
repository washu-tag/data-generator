package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.KoNullInstitution
import edu.washu.tag.generator.util.RandomGenUtils

/**
 * An entirely made-up PACS that could have generated some auxiliary series
 */
class SwiftPacs implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.ExplicitVRLittleEndian.toString()) : 95,
            (UID.ImplicitVRLittleEndian.toString()) : 5
    ])

    @Override
    Institution getInstitution() {
        new KoNullInstitution()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.SWIFT_PACS
    }

    @Override
    String getModelName() {
        'SwiftPACS'
    }

    @Override
    String getStationName() {
        'LTA-A0'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['1.6.5', '2.1a-3.1']
    }

    @Override
    String getDeviceSerialNumber() {
        null
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
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
