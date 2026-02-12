package edu.washu.tag.generator.query

import edu.washu.tag.QuerySourceDataProcessor
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.ExactNumberObjectsResult
import edu.washu.tag.validation.ExpectedQueryResult
import edu.washu.tag.validation.LoggableValidation

abstract class ExpectedMultipleRadReportQueryProcessor implements QuerySourceDataProcessor<BatchSpecification> {

    int expectedNumResults = 0
    LoggableValidation additionalValidation

    @Override
    void process(BatchSpecification batchSpecification) {
        batchSpecification.patients.each { patient ->
            patient.studies.each { study ->
                final RadiologyReport radiologyReport = study.radReport
                radiologyReport.setPatient(patient)
                radiologyReport.setStudy(study)
                expectedNumResults += countContribution(radiologyReport)
            }
        }
    }

    abstract int countContribution(RadiologyReport radiologyReport)

    @Override
    ExpectedQueryResult outputExpectation() {
        new ExactNumberObjectsResult(
            expectedNumResults: expectedNumResults,
            additionalValidation: additionalValidation
        )
    }

}
