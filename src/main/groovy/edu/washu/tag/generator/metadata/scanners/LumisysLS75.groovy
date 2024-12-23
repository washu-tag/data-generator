package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.SecondaryCaptureEquipment
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.enums.ConversionType
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.util.RandomGenUtils

@SuppressWarnings('ClashingTraitMethods') // default resolution is correct here
class LumisysLS75 implements SecondaryCaptureEquipment, SimpleRandomizedTransferSyntaxEquipment {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.JPEGLossless) : 50,
            (UID.JPEGExtended12Bit): 30,
            (UID.JPEGBaseline8Bit): 10,
            (UID.ImplicitVRLittleEndian)  : 10
    ])

    @Override
    String getDeviceId() {
        'LLS-0001'
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.LUMISYS
    }

    @Override
    String getModelName() {
        'LS75'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['LUMISYS RT-2000 v1.0']
    }

    @Override
    ConversionType getConversionType() {
        ConversionType.DIGITIZED_FILM
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

    @Override
    void encode(Attributes attributes) {
        encodeSecondaryCaptureEquipment(attributes)
    }

}
