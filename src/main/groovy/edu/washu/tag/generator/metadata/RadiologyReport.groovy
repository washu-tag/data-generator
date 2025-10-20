package edu.washu.tag.generator.metadata

import ca.uhn.hl7v2.DefaultHapiContext
import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.Varies
import ca.uhn.hl7v2.model.v281.datatype.EI
import ca.uhn.hl7v2.model.v281.datatype.NULLDT
import ca.uhn.hl7v2.model.v281.datatype.RP
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.util.idgenerator.UUIDGenerator
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.hl7.v2.model.TransportationMode
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.patient.PatientId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
    String placerOrderNumberId
    String placerOrderNumberNamespace
    TransportationMode transportationMode
    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = 'type') GeneratedReport generatedReport
    @JsonIgnore NULLDT deliverToLocation
    @JsonIgnore Parser parser

    @JsonIgnore
    protected abstract void createReport(HapiContext hapiContext, ORU_R01 baseReport)

    @JsonIgnore
    abstract ReportVersion getHl7Version()

    @JsonIgnore
    abstract DoctorEncoder getDoctorEncoder()

    @JsonIgnore
    abstract ObxGenerator getBaseObxGenerator(String content)

    @JsonIgnore
    abstract String getObservationIdSuffixForAddendum()

    void postProcess() {

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

    // Not very efficient, but shouldn't need to happen at scale
    @JsonIgnore
    String getReportTextForQueryExport() {
        final HapiContext hapiContext = hapiContext()
        final ORU_R01 baseReport = hapiContext.newMessage(ORU_R01)
        generatedReport.addObx(baseReport, this, getHl7Version())
        baseReport.PATIENT_RESULT.ORDER_OBSERVATION.getOBSERVATIONAll().collect { observation ->
            observation.OBX.getObx5_ObservationValue().collect { Varies obx5 ->
                final Type data = obx5.data
                if (data instanceof RP) {
                    final String asString = data.toString()
                    asString.substring(3, asString.length() - 1) // Strip out RP[ ... ]
                } else {
                    data.toString()
                }
            }.join('\n')
        }.join('\n')
    }

    @JsonIgnore
    Person getEffectivePrincipalInterpreter() {
        principalInterpreter ?: assistantInterpreters[0]
    }

    @JsonIgnore
    EI getPlacerOrderNumber() {
        final EI placerOrderNumber = new EI(null)
        placerOrderNumber.getEi1_EntityIdentifier().setValue(placerOrderNumberId)
        placerOrderNumber.getEi2_NamespaceID().setValue(placerOrderNumberNamespace)
        placerOrderNumber
    }

    @JsonIgnore
    EI getFillerOrderNumber() {
        final EI fillerOrderNumber = new EI(null)
        fillerOrderNumber.getEi1_EntityIdentifier().setValue(study.accessionNumber)
        fillerOrderNumber
    }

    @JsonIgnore
    Integer inferPatientAge() {
        final LocalDate dob = patient.dateOfBirth
        final LocalDateTime studyDateTime = study.studyDateTime()
        if (dob == null || studyDateTime == null) {
            null
        } else {
            ChronoUnit.YEARS.between(dob, studyDateTime)
        }
    }

    private static HapiContext hapiContext() {
        final HapiContext context = new DefaultHapiContext()
        context.getParserConfiguration().setIdGenerator(new UUIDGenerator())
        context.setValidationContext(new ValidationContextImpl())
        context
    }

}
