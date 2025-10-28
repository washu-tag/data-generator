package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.ExpectedQueryResult

import java.util.function.Function

class FirstPatientsRadReportResult extends ExpectedRadReportQueryProcessor implements WithColumnExtractions<FirstPatientsRadReportResult> {

    int numPatientsToMatch = 0
    List<String> matchedPatients = []
    ExactRowsResult expectation = new ExactRowsResult(uniqueIdColumnName: QueryUtils.COLUMN_MESSAGE_CONTROL_ID)

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

    @Override
    void includeReport(RadiologyReport radiologyReport) {
        expectation.rowAssertions.put(
            radiologyReport.messageControlId,
            columnExtractions.apply(radiologyReport)
        )
    }

    @Override
    ExpectedQueryResult outputExpectation() {
        expectation.setColumnTypes(columnTypes)
        expectation
    }

}
