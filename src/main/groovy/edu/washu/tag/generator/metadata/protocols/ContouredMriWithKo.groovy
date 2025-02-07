package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.ko.InstanceEnumerationKeyObjectNote
import edu.washu.tag.generator.metadata.seriesTypes.mr.Localizer
import edu.washu.tag.generator.metadata.seriesTypes.mr.T1Weighted
import edu.washu.tag.generator.metadata.seriesTypes.mr.T2Weighted
import edu.washu.tag.generator.metadata.seriesTypes.radiotherapy.RtstructSeriesType
import edu.washu.tag.generator.util.RandomGenUtils

class ContouredMriWithKo extends Protocol {

    private static final EnumeratedDistribution<String> studyDescriptionRandomizer = RandomGenUtils.setupWeightedLottery([
            'MRI W/ CONTRAST' : 40,
            "BRAIN MRI" : 40,
            'Brain' : 35,
            'MRI_AX_VIEWS_BRAIN' : 20,
            'DIAG MRI' : 10,
            'MRI BRAIN DIAGNOSTIC' : 10
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Localizer(anatomicalPlane : AnatomicalPlane.TRANSVERSE),
                new T1Weighted(anatomicalPlane : AnatomicalPlane.TRANSVERSE),
                new T2Weighted(anatomicalPlane : AnatomicalPlane.TRANSVERSE),
                new RtstructSeriesType('T1'),
                new InstanceEnumerationKeyObjectNote()
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.BRAIN]
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'brain MRI'
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('brain mri with contrast')
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        studyDescriptionRandomizer.sample()
    }

}
