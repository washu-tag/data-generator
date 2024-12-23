package edu.washu.tag.generator.metadata.scanners

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.util.RandomGenUtils

class AcusonSequoia implements Equipment {

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.ACUSON
    }

    @Override
    String getModelName() {
        'SEQUOIA'
    }

    @Override
    String getStationName() {
        'US_MOD-2'
    }

    @Override
    String getProtocolName(Study study, Series seriesType) {
        null
    }

    @Override
    List<String> getSoftwareVersions() {
        ['3.15']
    }

    @Override
    String getDeviceSerialNumber() {
        '51445'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        1
    }

    @Override
    @SuppressWarnings('GroovyMissingReturnStatement')
    String getTransferSyntaxUID(String sopClassUID) {
        switch (sopClassUID) {
            case UID.UltrasoundImageStorage :
                return RandomGenUtils.weightedCoinFlip(80) ? UID.RLELossless : UID.ImplicitVRLittleEndian
            case UID.UltrasoundMultiFrameImageStorage :
                return RandomGenUtils.weightedCoinFlip(90) ? UID.JPEGBaseline8Bit : UID.ImplicitVRLittleEndian
            default :
                reportUnsupportedSopClass(sopClassUID)
        }
    }

}
