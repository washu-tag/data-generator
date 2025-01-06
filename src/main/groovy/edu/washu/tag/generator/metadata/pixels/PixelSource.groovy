package edu.washu.tag.generator.metadata.pixels

import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import org.dcm4che3.data.Attributes

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
interface PixelSource {

    Attributes produceImage(Study study, Series series)

}