package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.Study
import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.seriesTypes.nm.MyometrixResults
import edu.washu.tag.generator.metadata.seriesTypes.nm.NmGatedTomo
import edu.washu.tag.generator.metadata.seriesTypes.nm.NmTomo
import edu.washu.tag.generator.metadata.seriesTypes.nm.RestingNm
import edu.washu.tag.generator.util.RandomGenUtils

class MyocardialPerfusion extends Protocol {

    private static final EnumeratedDistribution<String> randomizer = RandomGenUtils.setupWeightedLottery([
            'NM MYOCARDIAL PERFUSION SPECT MULTIPLE' : 100,
            'NM MPI SPECT' : 50,
            'MPI' : 30
    ])

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new RestingNm(),
                new NmTomo(),
                new NmGatedTomo(),
                new MyometrixResults()
        ]
    }

    @Override
    List<BodyPart> getApplicableBodyParts() {
        [BodyPart.HEART]
    }

    @Override
    String getStudyDescription(Equipment scanner, Study study) {
        randomizer.sample()
    }

    @Override
    ProcedureCode getProcedureCode(BodyPart bodyPart) {
        ProcedureCode.lookup('myocardial perf')
    }

    @Override
    String getSimpleDescription(BodyPart bodyPart) {
        'cardiac NM study'
    }

}
