package edu.washu.tag.generator.query

import edu.washu.tag.TestQuery
import edu.washu.tag.TestQuerySuite
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.enums.Sex

import java.util.function.Predicate

class QueryUtils {

    public static final String SPECIAL_CHAR_INSERT = 'Inserted statement for testing with special chars: ±60%÷\n'
    public static final String TABLE_NAME = TestQuerySuite.TABLE_PLACEHOLDER
    public static final String COLUMN_SEX = 'sex'
    public static final String COLUMN_HL7_VERSION = 'version_id'
    public static final String COLUMN_DOB = 'birth_date'
    public static final String COLUMN_MESSAGE_CONTROL_ID = 'message_control_id'
    public static final String COLUMN_REPORT_STATUS = 'report_status'
    public static final String COLUMN_MESSAGE_DT = 'message_dt'

    public static final List<Predicate<RadiologyReport>> classicReportsWithContentAndVersions = ReportVersion.values().collect { ReportVersion version ->
        classicReportWithContentAndVersion(version)
    }

    static Predicate<RadiologyReport> classicReportWithContentAndVersion(ReportVersion version) {
        reportOfClass(ClassicReport).and({ it.includeObx && it.hl7Version == version })
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

    static void setSqlFindMessageControlIds(TestQuery query, String select = '*') {
        final String reportIds = (query.querySourceDataProcessor as ExpectedRadReportQueryProcessor)
            .matchedReportIds.collect { "'${it}'".toString() }.join(', ')
        query.setSql("SELECT ${select} FROM ${TABLE_NAME} WHERE ${COLUMN_MESSAGE_CONTROL_ID} IN (${reportIds})")
    }

}
