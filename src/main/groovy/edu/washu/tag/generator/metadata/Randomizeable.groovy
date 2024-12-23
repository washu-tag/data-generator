package edu.washu.tag.generator.metadata

import org.apache.commons.math3.distribution.EnumeratedDistribution
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.util.StringReplacements

trait Randomizeable {

    String randomizeWithBodyPart(EnumeratedDistribution<String> enumeratedDistribution, BodyPart bodyPart) {
        enumeratedDistribution.sample().replace(StringReplacements.BODYPART, bodyPart.dicomRepresentation)
    }

}