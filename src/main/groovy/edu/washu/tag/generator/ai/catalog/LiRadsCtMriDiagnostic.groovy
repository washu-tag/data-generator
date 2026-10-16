package edu.washu.tag.generator.ai.catalog

import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.protocols.LiverCt
import edu.washu.tag.generator.metadata.protocols.LiverMri
import edu.washu.tag.generator.metadata.seriesTypes.ContrastPhased

class LiRadsCtMriDiagnostic extends LiRadsReport {

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        final List<String> phases = study.series*.seriesType.findAll { it instanceof ContrastPhased }*.phase*.displayName
        super.getUserMessage(study, studyRep) + 'This is a LI-RADS v2018 CT/MRI diagnostic report. The technique section should list the contrast agent and the acquired phases ' +
            "(${phases.join(', ')}), and the findings should only reference those phases. Observations should be part of the findings section and should specify hepatic segment, size in mm, " +
            'series and image number, the presence or absence of each major feature (nonrim arterial phase hyperenhancement, nonperipheral washout, enhancing capsule, threshold growth), ' +
            'and any ancillary features, and must end with its category written as LR-NC, LR-1, LR-2, LR-3, LR-4, LR-5, LR-M, or LR-TIV. On occasion, you should format these as, for example, ' +
            'LR 2 or LI-RADS 3 to simulate diversity in real reports. The category must be the single highest category among the observations. The impression should summarize each observation with ' +
            'its category and give management recommendations. '
    }

    @Override
    Boolean checkApplicability(Study study) {
        study.protocol instanceof LiverMri || study.protocol instanceof LiverCt
    }

}
