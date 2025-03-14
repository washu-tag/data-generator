package edu.washu.tag.generator.hl7.v2

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.metadata.Patient

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
@JsonSubTypes([
    @JsonSubTypes.Type(value = CyclicVariedGptGenerator, name = 'gpt'),
    @JsonSubTypes.Type(value = FixedSampleReportGenerator, name = 'fixed'),
    @JsonSubTypes.Type(value = FixedLargeSampleReportGenerator, name = 'fixed_large')
])
abstract class ReportGenerator {

    abstract void generateReportsForPatient(Patient patient)

}
