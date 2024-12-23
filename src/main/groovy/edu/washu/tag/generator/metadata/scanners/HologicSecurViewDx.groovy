package edu.washu.tag.generator.metadata.scanners

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.SouthGrandMammographySpecialty

class HologicSecurViewDx implements Equipment {

    @Override
    Institution getInstitution() {
        new SouthGrandMammographySpecialty()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.HOLOGIC
    }

    @Override
    String getModelName() {
        'SecurView'
    }

    @Override
    String getStationName() {
        'SGMS_WS_01'
    }

    @Override
    List<String> getSoftwareVersions() {
        ['8.3']
    }

    @Override
    String getDeviceSerialNumber() {
        '81A001F-10A'
    }

    @Override
    String getProtocolName(Study study, Series series) {
        null
    }

    @Override
    String getTransferSyntaxUID(String sopClassUID) {
        UID.ExplicitVRLittleEndian
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        1
    }

}
