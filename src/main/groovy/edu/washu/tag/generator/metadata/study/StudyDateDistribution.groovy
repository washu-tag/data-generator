package edu.washu.tag.generator.metadata.study

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.metadata.Patient

import java.time.LocalDate

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
@JsonSubTypes([
    @JsonSubTypes.Type(value = UniformStudyDateDistribution, name = 'uniform')
])
interface StudyDateDistribution {

    LocalDate generateStudyDate(Patient patient)

}