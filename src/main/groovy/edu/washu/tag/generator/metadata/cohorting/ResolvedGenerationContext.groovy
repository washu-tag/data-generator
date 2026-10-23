package edu.washu.tag.generator.metadata.cohorting

/**
 * The text actually used for a study's additionalGenerationContext, plus which
 * additionalGenerationContextOptions entry (if any) was chosen to produce it - null if the
 * step has no options and the base text was used unchanged.
 */
class ResolvedGenerationContext {

    String text
    String chosenOption

}
