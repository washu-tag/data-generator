package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.FirstMatchingReportsRadReportResult
import edu.washu.tag.validation.column.ArrayType
import edu.washu.tag.validation.column.ColumnType
import edu.washu.tag.validation.column.InstantType
import edu.washu.tag.validation.column.IntegerType
import edu.washu.tag.validation.column.LocalDateType

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

import static edu.washu.tag.generator.hl7.v2.ReportVersion.V2_3
import static edu.washu.tag.generator.hl7.v2.ReportVersion.V2_4
import static edu.washu.tag.generator.hl7.v2.ReportVersion.V2_7
import static edu.washu.tag.generator.query.QueryUtils.COLUMN_DOB
import static edu.washu.tag.generator.query.QueryUtils.COLUMN_MESSAGE_DT
import static edu.washu.tag.generator.query.QueryUtils.COLUMN_REPORT_STATUS
import static edu.washu.tag.generator.query.QueryUtils.COLUMN_SEX
import static edu.washu.tag.generator.query.QueryUtils.classicReportsWithContentAndVersions
import static edu.washu.tag.generator.query.QueryUtils.setSqlFindMessageControlIds

class ExtendedMetadata extends TestQuery<BatchSpecification> {

    private static final String COLUMN_PATIENT_AGE = 'patient_age'
    private static final String COLUMN_REQUESTED_DT = 'requested_dt'
    private static final String COLUMN_STATUS_CHANGE_DT = 'results_report_status_change_dt'
    private static final String COLUMN_YEAR = 'year'
    private static final String COLUMN_ASSISTANT_RESULT_INTERPRETER = 'assistant_result_interpreter'

    ExtendedMetadata() {
        super('extended_metadata', null)
        withDataProcessor(
            new FirstMatchingReportsRadReportResult(
                1, classicReportsWithContentAndVersions
            ).withColumnExtractions({ RadiologyReport radiologyReport ->
                final ProcedureCode procedureCode = ProcedureCode.lookup(radiologyReport.study.procedureCodeId)
                final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .appendLiteral('+00:00')
                    .toFormatter()
                [
                    'version_id': radiologyReport.hl7Version.hl7Version,
                    'mpi': radiologyReport.hl7Version == V2_3 ? radiologyReport.patient.legacyPatientId : null,
                    'sending_facility': 'ABCHOSP',
                    'service_identifier': procedureCode.codedTriplet.codeValue,
                    (COLUMN_SEX): radiologyReport.patient.sex.dicomRepresentation,
                    'zip_or_postal_code': '61111',
                    'country': radiologyReport.hl7Version == V2_3 ? '' : 'USA',
                    'orc_2_placer_order_number': getOrc2(radiologyReport),
                    'obr_2_placer_order_number': radiologyReport.placerOrderNumber.getEi1_EntityIdentifier().value,
                    (COLUMN_PATIENT_AGE): radiologyReport.hl7Version == V2_7 ? String.valueOf(radiologyReport.inferPatientAge()) : null,
                    'principal_result_interpreter': radiologyReport.principalInterpreter?.formatFirstLast()?.toUpperCase(),
                    (COLUMN_ASSISTANT_RESULT_INTERPRETER): radiologyReport.hl7Version == V2_3 ? '[]' : '[' + radiologyReport.assistantInterpreters*.formatFirstLast()*.toUpperCase().join(', ') + ']',
                    'technician': radiologyReport.hl7Version == V2_4 ? '[]' : "[${radiologyReport.technician.formatFirstLast().toUpperCase()}]".toString(),
                    'orc_3_filler_order_number': radiologyReport.hl7Version == V2_7 ? radiologyReport.study.accessionNumber : '',
                    'obr_3_filler_order_number': radiologyReport.study.accessionNumber,
                    'service_name': procedureCode.codedTriplet.codeMeaning,
                    'service_coding_system': procedureCode.codedTriplet.codingSchemeDesignator,
                    'diagnostic_service_id': ObrGenerator.derivePrimaryImagingModality(radiologyReport.study),
                    'study_instance_uid': radiologyReport.study.studyInstanceUid,
                    (COLUMN_DOB): DateTimeFormatter.ISO_LOCAL_DATE.format(radiologyReport.patient.dateOfBirth),
                    (COLUMN_MESSAGE_DT): dateTimeFormatter.format(radiologyReport.reportDateTime),
                    (COLUMN_REQUESTED_DT): radiologyReport.hl7Version == V2_7 ? dateTimeFormatter.format(radiologyReport.study.studyDateTime()) : null,
                    (COLUMN_STATUS_CHANGE_DT): dateTimeFormatter.format(radiologyReport.reportDateTime),
                    (COLUMN_REPORT_STATUS): radiologyReport.orcStatus.currentText,
                    'modality': ObrGenerator.derivePrimaryImagingModality(radiologyReport.study),
                    (COLUMN_YEAR): String.valueOf(radiologyReport.reportDateTime.getYear())
                ]
            }).withColumnTypes([
                new LocalDateType(COLUMN_DOB),
                new InstantType(COLUMN_MESSAGE_DT),
                new InstantType(COLUMN_REQUESTED_DT),
                new InstantType(COLUMN_STATUS_CHANGE_DT),
                new IntegerType(COLUMN_YEAR),
                new IntegerType(COLUMN_PATIENT_AGE),
                new ArrayType(COLUMN_ASSISTANT_RESULT_INTERPRETER)
            ] as Set<ColumnType<?>>)
        ).withPostProcessing({ query ->
            setSqlFindMessageControlIds(query)
        })
    }

    private String getOrc2(RadiologyReport radiologyReport) {
        switch (radiologyReport.hl7Version) {
            case V2_7 -> radiologyReport.placerOrderNumber.getEi1_EntityIdentifier().value
            case V2_4 -> null
            case V2_3 -> ''
        }
    }

}
