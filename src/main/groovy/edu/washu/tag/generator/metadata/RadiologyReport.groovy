package edu.washu.tag.generator.metadata

import ca.uhn.hl7v2.DefaultHapiContext
import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.v281.datatype.EI
import ca.uhn.hl7v2.model.v281.datatype.NULLDT
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.util.idgenerator.UUIDGenerator
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.patient.PatientId
import edu.washu.tag.generator.util.RandomGenUtils

import java.time.LocalDateTime

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
abstract class RadiologyReport {

    @JsonIgnore Patient patient
    @JsonIgnore Study study
    LocalDateTime reportDateTime
    String messageControlId
    List<PatientId> patientIds
    boolean includeAlias
    Race race
    boolean specifyAddress
    boolean extendedPid
    boolean malformInterpretersTechnician
    boolean includeObx
    Person technician
    List<Person> attendingDoctors
    String visitNumber
    Person orderingProvider
    Person principalInterpreter
    List<Person> assistantInterpreters
    ReportStatus orcStatus
    String reasonForStudy
    GeneratedReport generatedReport
    @JsonIgnore EI placerOrderNumber
    @JsonIgnore EI fillerOrderNumber
    @JsonIgnore NULLDT deliverToLocation
    @JsonIgnore Parser parser

    @JsonIgnore
    protected abstract void createReport(HapiContext hapiContext, ORU_R01 baseReport)

    @JsonIgnore
    abstract String getHl7Version()

    void postProcess() {
        final EI placerOrderNumber = new EI(null)
        placerOrderNumber.getEi1_EntityIdentifier().setValue(RandomGenUtils.randomIdStr())
        placerOrderNumber.getEi2_NamespaceID().setValue('SYS')
        setPlacerOrderNumber(placerOrderNumber)

        final EI fillerOrderNumber = new EI(null)
        fillerOrderNumber.getEi1_EntityIdentifier().setValue(study.accessionNumber)
        setFillerOrderNumber(fillerOrderNumber)
    }

    @JsonIgnore
    ORU_R01 produceReport(Patient patient, Study study) {
        final HapiContext hapiContext = hapiContext()
        setPatient(patient)
        setStudy(study)
        postProcess()
        final ORU_R01 baseReport = hapiContext.newMessage(ORU_R01)
        createReport(hapiContext, baseReport)
        if (parser != null) {
            baseReport.setParser(parser)
        }
        baseReport
    }

    @JsonIgnore
    Person getEffectivePrincipalInterpreter() {
        principalInterpreter ?: assistantInterpreters[0]
    }

    private static HapiContext hapiContext() {
        final HapiContext context = new DefaultHapiContext()
        context.getParserConfiguration().setIdGenerator(new UUIDGenerator())
        context.setValidationContext(new ValidationContextImpl())
        context
    }

}
