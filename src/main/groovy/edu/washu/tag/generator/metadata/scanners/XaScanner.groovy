package edu.washu.tag.generator.metadata.scanners

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.metadata.seriesTypes.xa.XaImageType

interface XaScanner {

    @JsonIgnore
    String getStudyDescription()

    @JsonIgnore
    String getSeriesDescription()

    @JsonIgnore
    XaImageType getImageType()

}
