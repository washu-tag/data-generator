package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.ExpectedQueryResult

import java.util.function.Function

class FirstPatientsRadReportResult extends ExpectedRadReportQueryProcessor {

    int numPatientsToMatch = 0
    List<String> matchedPatients = []
    Function<RadiologyReport, Map<String, String>> columnExtractions = { [:] }
    ExactRowsResult expectation = new ExactRowsResult(uniqueIdColumnName: 'message_control_id')

    FirstPatientsRadReportResult() {
        inclusionCriteria = { RadiologyReport radiologyReport ->
            final String patientUid = radiologyReport.patient.patientInstanceUid
            if (matchedPatients.size() < numPatientsToMatch || patientUid in matchedPatients) {
                if (!(patientUid in matchedPatients)) {
                    matchedPatients << patientUid
                }
                true
            } else {
                false
            }
        }
    }

    FirstPatientsRadReportResult(int numPatientsToMatch) {
        this()
        this.numPatientsToMatch = numPatientsToMatch
    }

    FirstPatientsRadReportResult withColumnExtractions(Function<RadiologyReport, Map<String, String>> columnExtractions) {
        this.columnExtractions = columnExtractions
        this
    }

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectation.rowAssertions.put(
            radiologyReport.messageControlId,
            columnExtractions.apply(radiologyReport)
        )
    }

    @Override
    ExpectedQueryResult outputExpectation() {
        expectation
    }

}
