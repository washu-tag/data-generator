package edu.washu.tag.generator.metadata.enums

import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution

/**
 * Acquisition phases of a multiphase liver CT/MRI, using LI-RADS v2018 terminology. Series description labels are
 * modeled on real series descriptions in the TCIA TCGA-LIHC collection. Post-contrast MR series frequently carry only
 * a generic "post" label rather than naming the phase.
 */
enum ContrastPhase {

    PRECONTRAST (
        'precontrast',
        ['Non Contrast' : 30, 'NON-CONTRAST' : 20, 'W/O' : 20, 'Pre Contrast' : 20, 'UNENHANCED' : 10],
        ['pre' : 40, 'PRE' : 40, 'Pre' : 20]
    ),
    LATE_ARTERIAL (
        'late arterial',
        ['Arterial' : 30, 'Late art. phase' : 25, 'Arterial phase' : 20, 'Late Arterial' : 15, 'ARTERIAL' : 10],
        ['post' : 30, 'POST' : 20, 'Arterial phase' : 20, 'late_arterial' : 15, 'ARTERIAL' : 15]
    ),
    PORTAL_VENOUS (
        'portal venous',
        ['P.Venous' : 30, 'Portal' : 20, 'portal venous' : 15, 'VENOUS' : 15, 'Venous phase' : 10, 'PV' : 10],
        ['post' : 30, 'POST' : 20, 'Portal phase' : 20, 'porto_venous' : 15, '1MIN POST' : 15]
    ),
    DELAYED (
        'delayed',
        ['Delay' : 30, '180 sec Delay' : 25, 'DELAYED' : 25, '5 MIN DELAY' : 20],
        ['post' : 25, 'POST' : 15, '5 min delay' : 25, 'DELAYED' : 20, '5 MIN POST GAD' : 15]
    ),
    // transitional and hepatobiliary phases only exist for MRI with a hepatobiliary agent (gadoxetate)
    TRANSITIONAL (
        'transitional',
        [:],
        ['post' : 45, 'POST' : 30, '3 min' : 25]
    ),
    HEPATOBILIARY (
        'hepatobiliary',
        [:],
        ['20 min delay' : 35, '(20 min) post gad' : 25, 'POST 20 MIN' : 25, 'HBP' : 15]
    )

    final String displayName
    private final EnumeratedDistribution<String> ctLabelRandomizer
    private final EnumeratedDistribution<String> mrLabelRandomizer

    ContrastPhase(String displayName, Map<String, Integer> ctLabelWeights, Map<String, Integer> mrLabelWeights) {
        this.displayName = displayName
        ctLabelRandomizer = ctLabelWeights ? RandomGenUtils.setupWeightedLottery(ctLabelWeights) : null
        mrLabelRandomizer = RandomGenUtils.setupWeightedLottery(mrLabelWeights)
    }

    String sampleCtLabel() {
        if (ctLabelRandomizer == null) {
            throw new UnsupportedOperationException("${name()} is not a CT phase")
        }
        ctLabelRandomizer.sample()
    }

    String sampleMrLabel() {
        mrLabelRandomizer.sample()
    }

}
