package edu.washu.tag.generator.metadata.scanners

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SimpleRandomizedTransferSyntaxEquipment
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Manufacturer
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.metadata.seriesTypes.xa.XaImageType
import edu.washu.tag.generator.util.RandomGenUtils

import java.util.concurrent.ThreadLocalRandom

import static edu.washu.tag.generator.util.StringReplacements.*

class SiemensAxiomArtis implements SimpleRandomizedTransferSyntaxEquipment, XaScanner {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'CARDIAC CATHETERIZATION' : 100,
            'Fluoroscopy NoRad' : 50,
            'Fluoroscopy' : 30,
            'FL FLUOROSCOPY' : 30,
            'CARDIAC CATHETERIZATION - DIAGNOSTIC' : 20,
            'Cardiac Cath' : 10,
            'HEART CATH' : 10
    ])

    private static final EnumeratedDistribution<String> seriesDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            (BODYPART) : 100,
            ("${BODYPART} ${FPS}".toString()) : 50,
            'Fluoro Normal' : 50,
            'Fluoro' : 30,
            'FL Normal Dose' : 20,
            ("${BODYPART} ${VIEW}".toString()) : 10,
            'Fluoro   Angio' : 10
    ])

    private static final EnumeratedDistribution<String> transferSyntaxRandomizer = RandomGenUtils.setupWeightedLottery([
            (UID.JPEGLossless)          : 50,
            (UID.ExplicitVRLittleEndian): 30,
            (UID.ImplicitVRLittleEndian): 10,
            (UID.JPEGExtended12Bit)        : 10
    ])

    private static final List<String> bodyParts = [
            'Aortic Arch',
            "${LATERALITY} Coronary Artery",
            "${LATERALITY} Coronary"
    ]

    private static final List<String> lateralities = ['L', 'Lt', 'LT', 'R', 'Rt', 'RT']
    private static final List<String> views = ['Oblique', 'Obl', 'OBL']
    private static final List<String> fpsValues = ['1', '3', '6', '15']
    private static final List<String> fpsRepresentations = ['F/S', 'fps']

    @Override
    Institution getInstitution() {
        new ChestertonAdamsHospital()
    }

    @Override
    Manufacturer getManufacturer() {
        Manufacturer.SIEMENS_LOWERCASE
    }

    @Override
    String getModelName() {
        'AXIOM-Artis'
    }

    @Override
    String getStationName() {
        'xa-saa0'
    }

    @Override
    String getStudyDescription() {
        studyDescriptionRandomizer.sample()
    }

    @Override
    String getSeriesDescription() {
        seriesDescriptionRandomizer.sample().
                replace(BODYPART, RandomGenUtils.randomListEntry(bodyParts)).
                replace(LATERALITY, RandomGenUtils.randomListEntry(lateralities)).
                replace(VIEW, RandomGenUtils.randomListEntry(views)).
                replace(FPS, "${RandomGenUtils.randomListEntry(fpsValues)} ${RandomGenUtils.randomListEntry(fpsRepresentations)}")
    }

    @Override
    String getProtocolName(Study study, Series series) {
        series.seriesDescription
    }

    @Override
    List<String> getSoftwareVersions() {
        ['VB11A']
    }

    @Override
    String getDeviceSerialNumber() {
        '1323.19909.33087102'
    }

    @Override
    XaImageType getImageType() {
        new XaImageType(original : false, primary : false).
                addValue('SINGLE ' + ThreadLocalRandom.current().nextBoolean() ? 'A' : 'B').
                addValue('STORE MONITOR')
    }

    @Override
    EnumeratedDistribution<String> getTransferSyntaxRandomizer() {
        transferSyntaxRandomizer
    }

}
