package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.CenterForSpecializedRadiology
import edu.washu.tag.generator.metadata.seriesTypes.xa.XaImageType
import edu.washu.tag.generator.util.RandomGenUtils

import static edu.washu.tag.generator.util.StringReplacements.*

class PhilipsAlluraXper implements SimpleRandomizedTransferSyntaxEquipment, XaScanner {

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.JPEGLossless) : 50,
            (UID.ExplicitVRLittleEndian) : 30,
            (UID.ImplicitVRLittleEndian) : 20
    ])

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'CARDIAC CATHETERIZATION' : 100,
            'Special Procedures' : 80,
            'Fluoroscopy NoRad' : 50,
            'Fluoroscopy' : 30,
            'FL FLUOROSCOPY' : 30,
            'CARDIAC CATHETERIZATION - DIAGNOSTIC' : 20,
            'Cardiac Cath' : 10,
            'HEART CATH' : 10
    ])

    private static final List<String> bodyParts = [
            'Aortic Arch',
            "${LATERALITY} Coronary Artery",
            "${LATERALITY} Coronary"
    ]

    private static final String BASE_SERIES_DESCRIPTION = "${BODYPART} ${FPS}"

    private static final List<String> lateralities = ['L', 'Left', 'R', 'Right']
    private static final List<String> fpsValues = ['1', '3', '6', '15']
    private static final List<String> fpsRepresentations = ['F/S', 'fps']

    @Override
    Institution getInstitution() {
        new CenterForSpecializedRadiology()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.PHILIPS
    }

    @Override
    String getModelName() {
        'AlluraXper'
    }

    @Override
    String getStationName() {
        'CC-XA-01'
    }

    @Override
    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        seriesIndex + 1
    }

    @Override
    String getStudyDescription() {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSeriesDescription() {
        BASE_SERIES_DESCRIPTION.
                replace(BODYPART, RandomGenUtils.randomListEntry(bodyParts)).
                replace(LATERALITY, RandomGenUtils.randomListEntry(lateralities)).
                replace(FPS, "${RandomGenUtils.randomListEntry(fpsValues)} ${RandomGenUtils.randomListEntry(fpsRepresentations)}")
    }

    @Override
    String getProtocolName(Study study, Series series) {
        study.studyDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['Allura Xper, 8.1.2.1']
    }

    @Override
    String getDeviceSerialNumber() {
        '865217-210'
    }

    @Override
    XaImageType getImageType() {
        new XaImageType(original: false).addValue('SINGLE A')
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
