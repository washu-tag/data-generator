package edu.washu.tag.generator.hl7.v2

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.CustomGeneratedReportGuarantee
import edu.washu.tag.generator.metadata.Patient

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
@JsonSubTypes([
    @JsonSubTypes.Type(value = CyclicVariedGptGenerator, name = 'gpt'),
    @JsonSubTypes.Type(value = FixedSampleReportGenerator, name = 'fixed')
])
abstract class ReportGenerator {

    abstract void generateReportsForPatients(List<Patient> patients, boolean temporalHeartbeat, List<CustomGeneratedReportGuarantee> reportGuarantees = [])

}
