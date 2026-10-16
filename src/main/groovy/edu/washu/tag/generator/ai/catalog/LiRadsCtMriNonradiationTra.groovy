package edu.washu.tag.generator.ai.catalog

import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.protocols.LiverCt
import edu.washu.tag.generator.metadata.protocols.LiverMri
import edu.washu.tag.generator.metadata.seriesTypes.ContrastPhased

/**
 * Treatment response report following the LI-RADS CT/MRI Nonradiation TRA v2024 Core.
 */
class LiRadsCtMriNonradiationTra extends LiRadsReport {

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        final List<String> phases = study.series*.seriesType.findAll { it instanceof ContrastPhased }*.phase*.displayName
        final String ancillaryFeatures = study.protocol instanceof LiverMri ?
            'Diffusion restriction or mild-moderate T2 hyperintensity in an area of uncertain enhancement may upgrade LR-TR Equivocal to LR-TR Viable. ' : ''
        super.getUserMessage(study, studyRep) + 'This is a LI-RADS v2024 CT/MRI Nonradiation Treatment Response (TRA) report after a nonradiation therapy such as ' +
            'ablation, TACE, or resection; name the treatment in the history. ' +
            "The technique section should list the contrast agent and the acquired phases (${phases.join(', ')}), and the findings should only reference those phases. " +
            'For each treated observation, give the segment, series and image number, pretreatment category, change since prior, and an LR-TR category based on ' +
            'masslike enhancement (an enhancing area of any degree in any phase that occupies space) in or along the treated lesion: LR-TR Viable if present, ' +
            'LR-TR Equivocal if uncertain, LR-TR Nonviable if absent (such as no lesional enhancement, or only a smooth rim or perfusional enhancement), or ' +
            'LR-TR Nonevaluable if it cannot be assessed due to image degradation. ' + ancillaryFeatures +
            'If unsure, choose LR-TR Equivocal. Measure the largest masslike enhancing component for LR-TR Viable or LR-TR Equivocal. ' +
            'Give untreated observations a diagnostic category (LR-1 to LR-5, LR-M, or LR-TIV). State categories in both the findings and the impression. ' +
            'The category must be the LR-TR category of the most concerning treated observation (Viable, then Equivocal, then Nonviable; Nonevaluable only if ' +
            'none can be evaluated). In the impression, recommend follow-up imaging in about ' +
            '3 months, or multidisciplinary discussion for LR-TR Viable. '
    }

    @Override
    Boolean checkApplicability(Study study) {
        study.protocol instanceof LiverMri || study.protocol instanceof LiverCt
    }

}
