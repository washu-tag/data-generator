package edu.washu.tag.generator.metadata.scanners

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.metadata.ImageType

trait CtScanner {

    @JsonIgnore
    abstract ImageType getTopogramImageType()

    @JsonIgnore
    abstract ImageType getAttenuationCorrectedCtImageType()

    ImageType getCtImageType(boolean isLocalizer) {
        new ImageType().addValue(isLocalizer ? 'LOCALIZER' : 'AXIAL')
    }

}