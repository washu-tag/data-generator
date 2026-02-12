package edu.washu.tag.generator.query

import edu.washu.tag.TestQuery
import edu.washu.tag.TestQuerySuite
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Sex

import java.util.function.Function
import java.util.function.Predicate

class QueryUtils {

    public static final String SPECIAL_CHAR_INSERT = 'Inserted statement for testing with special chars: ±60%÷\n'
    public static final String TABLE_NAME = TestQuerySuite.TABLE_PLACEHOLDER
    public static final String SUFFIX_CURATED = '_curated'
    public static final String SUFFIX_LATEST = '_latest'
    public static final String SUFFIX_DIAGNOSES = '_dx'
    public static final String COLUMN_SEX = 'sex'
    public static final String COLUMN_HL7_VERSION = 'version_id'
    public static final String COLUMN_DOB = 'birth_date'
    public static final String COLUMN_MESSAGE_CONTROL_ID = 'message_control_id'
    public static final String COLUMN_REPORT_STATUS = 'report_status'
    public static final String COLUMN_MESSAGE_DT = 'message_dt'
    public static final String COLUMN_PATIENT_IDS = 'patient_ids'
    public static final String COLUMN_DIAGNOSIS_CODE = 'diagnosis_code'
    public static final String COLUMN_DIAGNOSES = 'diagnoses'
    public static final String COLUMN_DIAGNOSES_CONSOLIDATED = 'diagnoses_consolidated'

    public static final Predicate<RadiologyReport> REPORTS_WITH_CONTENT = { RadiologyReport radiologyReport ->
        radiologyReport.includeObx
    }

    public static final List<Predicate<RadiologyReport>> classicReportsWithContentAndVersions = ReportVersion.values().collect { ReportVersion version ->
        classicReportWithContentAndVersion(version)
    }

    static Predicate<RadiologyReport> reportOfClassWithContentAndVersion(Class<? extends GeneratedReport> reportClass, ReportVersion version) {
        reportOfClass(reportClass).and(reportOfVersion(version)).and(REPORTS_WITH_CONTENT)
    }

    static Predicate<RadiologyReport> classicReportWithContentAndVersion(ReportVersion version) {
        reportOfClassWithContentAndVersion(ClassicReport, version)
    }

    static Predicate<RadiologyReport> reportOfVersion(ReportVersion version) {
        { RadiologyReport radiologyReport ->
            radiologyReport.hl7Version == version
        }
    }

    static Predicate<RadiologyReport> reportOfClass(Class<? extends GeneratedReport> reportClass) {
        { RadiologyReport radiologyReport ->
            reportClass.isInstance(radiologyReport.generatedReport)
        }
    }

    static Predicate<RadiologyReport> reportWithMessageControlId(String messageControlId) {
        { RadiologyReport radiologyReport ->
            radiologyReport.messageControlId == messageControlId
        }
    }

    static Predicate<RadiologyReport> matchesHl7Version(String version) {
        { RadiologyReport radiologyReport ->
            radiologyReport.hl7Version.hl7Version == version
        }
    }

    static Predicate<RadiologyReport> doesNotMatchHl7Version(String version) {
        matchesHl7Version(version).negate()
    }

    static Predicate<RadiologyReport> sexFilter(Sex sex) {
        { RadiologyReport radiologyReport ->
            radiologyReport.patient.sex == sex
        }
    }

    static void setSqlFindMessageControlIds(TestQuery query, String select = '*', String tableSuffix = '') {
        final String reportIds = (query.querySourceDataProcessor as WithMatchedReportIds)
            .matchedReportIds.collect { "'${it}'".toString() }.join(', ')
        query.setSql("SELECT ${select} FROM ${TABLE_NAME}${tableSuffix} WHERE ${COLUMN_MESSAGE_CONTROL_ID} IN (${reportIds})")
    }

    static void setSqlSelectStarFindMessageControlIds(TestQuery query, String tableSuffix = '') {
        setSqlFindMessageControlIds(query, '*', tableSuffix)
    }

    static <X> String serializeArrayOfStruct(List<X> inputs, Function<X, String> objectSerializer) {
        final String serializedObjects = inputs.findResults { input ->
            input != null ? objectSerializer.apply(input) : null
        }.join(', ')
        "[${serializedObjects}]"
    }

    static <X> String serializeArrayOfStructByProperties(List<X> inputs, Function<X, List<String>> objectComponents) {
        serializeArrayOfStruct(
            inputs,
            { input ->
                serializeStructByProperties(objectComponents.apply(input))
            }
        )
    }

    static String serializeStructByProperties(List<String> objectComponents) {
        "[${objectComponents.join(',')}]"
    }

}
