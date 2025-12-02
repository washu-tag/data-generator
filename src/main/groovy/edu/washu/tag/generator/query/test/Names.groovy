package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder2_7
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.FirstMatchingReportsRadReportResult
import edu.washu.tag.validation.column.*

import static edu.washu.tag.generator.hl7.v2.ReportVersion.*
import static edu.washu.tag.generator.query.QueryUtils.*

class Names extends TestQuery<BatchSpecification> {

    private static final String COLUMN_FULL_PATIENT_NAME = 'full_patient_name'
    private static final String COLUMN_FULL_ORDERING_PROVIDER = 'full_ordering_provider'
    private static final String COLUMN_FULL_PRINCIPAL_INTERPRETER = 'full_principal_result_interpreter'
    private static final String COLUMN_FULL_ASSISTANT_INTERPRETER = 'full_assistant_result_interpreter'
    private static final String COLUMN_FULL_TECHNICIAN = 'full_technician'
    private static final String COLUMN_ASSISTANT_INTERPRETER = 'assistant_result_interpreter'
    private static final String COLUMN_TECHNICIAN = 'technician'

    Names() {
        super('extended_metadata', null)
        withDataProcessor(
            new FirstMatchingReportsRadReportResult(
                1,
                classicReportsWithContentAndVersions.collect {
                    it.and({ it.technician != null })
                }
            ).withColumnExtractions({ RadiologyReport radiologyReport ->
                final Person patientName = radiologyReport.patient.patientName
                final ReportVersion reportVersion = radiologyReport.hl7Version
                [
                    'version_id': reportVersion.hl7Version,
                    (COLUMN_FULL_PATIENT_NAME): serializeStructByProperties([
                        patientName.familyNameAlphabetic.toUpperCase(),
                        patientName.givenNameAlphabetic.toUpperCase(),
                        patientName.middleNameAlphabetic?.toUpperCase(),
                        patientName.suffixAlphabetic?.toUpperCase(),
                        patientName.prefixAlphabetic?.toUpperCase(),
                        patientName.degree?.toUpperCase(),
                        reportVersion != V2_3 ? 'D' : null
                    ]),
                    'patient_name': patientName.formatFirstLast(),
                    (COLUMN_FULL_ORDERING_PROVIDER): getObr16(radiologyReport),
                    'ordering_provider': reportVersion == V2_3 ? null : radiologyReport.orderingProvider.formatFirstLast(),
                    (COLUMN_FULL_PRINCIPAL_INTERPRETER): getObr32(radiologyReport),
                    'principal_result_interpreter': radiologyReport.principalInterpreter?.formatFirstLast()?.toUpperCase(),
                    (COLUMN_FULL_ASSISTANT_INTERPRETER): getObr33(radiologyReport),
                    (COLUMN_ASSISTANT_INTERPRETER): radiologyReport.hl7Version == V2_3 ? '[]' : '[' + radiologyReport.assistantInterpreters*.formatFirstLast()*.toUpperCase().join(', ') + ']',
                    (COLUMN_FULL_TECHNICIAN): getObr34(radiologyReport),
                    (COLUMN_TECHNICIAN): radiologyReport.hl7Version == V2_4 ? '[]' : "[${radiologyReport.technician.formatFirstLast().toUpperCase()}]".toString()
                ]
            }).withColumnTypes([
                new ArrayType(COLUMN_FULL_PATIENT_NAME),
                new ArrayType(COLUMN_FULL_ORDERING_PROVIDER),
                new ArrayType(COLUMN_FULL_PRINCIPAL_INTERPRETER),
                new ArrayType(COLUMN_FULL_ASSISTANT_INTERPRETER),
                new ArrayType(COLUMN_ASSISTANT_INTERPRETER),
                new ArrayType(COLUMN_FULL_TECHNICIAN),
                new ArrayType(COLUMN_TECHNICIAN)
            ] as Set<ColumnType<?>>)
        ).withPostProcessing({ query ->
            setSqlFindMessageControlIds(query)
        })
    }

    private String getObr16(RadiologyReport radiologyReport) {
        serializeArrayOfStructByProperties(
            [radiologyReport.hl7Version != V2_3 ? radiologyReport.orderingProvider : null],
            { person ->
                [
                    person.personIdentifier,
                    person.familyNameAlphabetic.toUpperCase(),
                    person.givenNameAlphabetic.toUpperCase(),
                    radiologyReport.hl7Version == V2_7 ? person.middleInitial()?.toUpperCase() : person.middleNameAlphabetic,
                    person.suffixAlphabetic,
                    person.prefixAlphabetic,
                    person.degree,
                    null, // name type code not set here
                    person.assigningAuthority?.namespaceId,
                    radiologyReport.hl7Version == V2_7 ? DoctorEncoder2_7.HOSP : null,
                    null // assigning facility not set here
                ]
            }
        )
    }

    private String serializePseudoCnn(DoctorEncoder doctorEncoder, List<Person> names, boolean nullOutId = false) {
        serializeArrayOfStructByProperties(
            names,
            { name -> [
                (!nullOutId) ? name.personIdentifier : null,
                name.familyNameAlphabetic.toUpperCase(),
                name.givenNameAlphabetic.toUpperCase(),
                name.middleInitial()?.toUpperCase(),
                null, // suffix not currently set here
                null, // prefix not currently set here
                doctorEncoder.includeDegree() ? name.degree : null,
                null, // no name type code
                doctorEncoder.includeAssigningAuthority() ? name.assigningAuthority?.namespaceId : null,
                null, // no identifier type code
                null // no assigning facility
            ]}
        )
    }

    private String getObr32(RadiologyReport radiologyReport) {
        serializePseudoCnn(
            radiologyReport.doctorEncoder,
            [radiologyReport.hl7Version != V2_7 ? radiologyReport.principalInterpreter : null]
        )
    }

    private String getObr33(RadiologyReport radiologyReport) {
        serializePseudoCnn(
            radiologyReport.doctorEncoder,
            radiologyReport.hl7Version != V2_3 ? radiologyReport.assistantInterpreters : []
        )
    }

    private String getObr34(RadiologyReport radiologyReport) {
        serializePseudoCnn(
            radiologyReport.doctorEncoder,
            [radiologyReport.hl7Version != V2_4 ? radiologyReport.technician :  null],
            radiologyReport.hl7Version == V2_3
        )
    }

}
