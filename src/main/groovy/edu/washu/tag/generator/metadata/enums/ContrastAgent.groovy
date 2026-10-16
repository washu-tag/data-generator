package edu.washu.tag.generator.metadata.enums

/**
 * Contrast agent classes for liver MRI as distinguished by the LI-RADS v2018 CT/MRI technical recommendations.
 * Gadobenate is a hepatobiliary agent, but LI-RADS groups it with extracellular agents: it uses the same required
 * phases, and its optional hepatobiliary phase (1 to 3 hours after injection) is typically not obtained.
 */
enum ContrastAgent {

    EXTRACELLULAR (null, [ContrastPhase.DELAYED]),
    GADOBENATE ('gadobenate', [ContrastPhase.DELAYED]),
    GADOXETATE ('gadoxetate', [ContrastPhase.TRANSITIONAL, ContrastPhase.HEPATOBILIARY])

    final String agentName
    final List<ContrastPhase> phasesAfterPortalVenous

    ContrastAgent(String agentName, List<ContrastPhase> phasesAfterPortalVenous) {
        this.agentName = agentName
        this.phasesAfterPortalVenous = phasesAfterPortalVenous
    }

}
