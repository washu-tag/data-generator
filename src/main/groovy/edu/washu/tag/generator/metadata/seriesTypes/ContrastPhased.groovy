package edu.washu.tag.generator.metadata.seriesTypes

import edu.washu.tag.generator.metadata.enums.ContrastPhase

/**
 * A series type acquired at a single contrast phase of a multiphase exam.
 */
interface ContrastPhased {

    ContrastPhase getPhase()

}
