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

class PhilipsMobileDiagnostwDR implements SimpleRandomizedTransferSyntaxEquipment {

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
        Manufacturer.PHILIPS
    }

    @Override
    String getModelName() {
        'MobileDiagnost wDR'
    }

    @Override
    String getStationName() {
        'CAH_XR_P_1A'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        "Portable ${study.bodyPartExamined.code.codeMeaning}"
    }

    @Override
    List<String> getSoftwareVersions() {
        ['1.1.3', 'PMS81.101.1.1 GXR GXRIM10.0'] // copied from a public TCIA example, not really sure what these components are [other than PMS = Philips Medical Systems, probably]
    }

    @Override
    String getDeviceSerialNumber() {
        '7324AE-4100B'
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
