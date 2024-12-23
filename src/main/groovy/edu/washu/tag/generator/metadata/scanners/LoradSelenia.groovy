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

class LoradSelenia implements SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.JPEGLossless): 50,
            (UID.ExplicitVRLittleEndian) : 30,
            (UID.ImplicitVRLittleEndian): 10,
            (UID.JPEG2000Lossless) : 10
    ])

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.LORAD
    }

    @Override
    String getModelName() {
        'Lorad Selenia'
    }

    @Override
    String getStationName() {
        'selenia_01'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['AWS:MAMMODROC_3_4_1_8', 'PXCM:1.4.0.7', 'ARR:1.7.3.10', 'IP:4.9.0'] // copied from a public TCIA example, not really sure what these components are
    }

    @Override
    String getDeviceSerialNumber() {
        'H1KRHR8426d1b2'
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
