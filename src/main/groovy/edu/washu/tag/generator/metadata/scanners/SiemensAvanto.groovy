package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.util.RandomGenUtils

class SiemensAvanto implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.JPEGLossless) : 50,
            (UID.ExplicitVRLittleEndian) : 30,
            (UID.ImplicitVRLittleEndian) : 10,
            (UID.JPEGExtended12Bit) : 10
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
        'Avanto'
    }

    @Override
    String getStationName() {
        'MRC21815'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['syngo MR D13']
    }

    @Override
    String getDeviceSerialNumber() {
        'S-000100739831023181-A712'
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
