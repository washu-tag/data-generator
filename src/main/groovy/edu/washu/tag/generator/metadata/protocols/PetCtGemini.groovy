package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.seriesTypes.ct.CtWithAttenuationCorrection
import edu.washu.tag.generator.metadata.seriesTypes.ct.Topogram
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtNonAttenuationCorrected
import edu.washu.tag.generator.metadata.seriesTypes.pt.PtWithAttenuationCorrection

class PetCtGemini extends PetCt {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new Topogram(),
                new CtWithAttenuationCorrection(),
                new PtWithAttenuationCorrection(),
                new PtNonAttenuationCorrected()
        ]
    }

}
