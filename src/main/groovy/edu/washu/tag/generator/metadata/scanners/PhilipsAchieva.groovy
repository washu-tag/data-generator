package edu.washu.tag.generator.metadata.scanners

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital

class PhilipsAchieva implements Equipment {

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
        'Achieva'
    }

    @Override
    String getStationName() {
        'PHILIPS-8A11CE4'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['2.6.3', '2.6.3.3']
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        "${seriesIndex + 4}01".toInteger()
    }

    @Override
    String getDeviceSerialNumber() {
        '26344'
    }

    @Override
    String getTransferSyntaxUID(String sopClassUID) {
        UID.ExplicitVRLittleEndian
    }

}
