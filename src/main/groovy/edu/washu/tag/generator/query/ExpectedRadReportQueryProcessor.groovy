package edu.washu.tag.generator.query

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.QuerySourceDataProcessor
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.RadiologyReport

import java.util.function.Function
import java.util.function.Predicate

abstract class ExpectedRadReportQueryProcessor implements QuerySourceDataProcessor<BatchSpecification>, WithMatchedReportIds {

    @JsonIgnore
    Predicate<RadiologyReport> inclusionCriteria

    @Override
    void process(BatchSpecification batchSpecification) {
        batchSpecification.patients.each { patient ->
            patient.studies.each { study ->
                final RadiologyReport radiologyReport = study.radReport
                radiologyReport.setPatient(patient)
                radiologyReport.setStudy(study)
                if (inclusionCriteria.test(radiologyReport)) {
                    includeReport(radiologyReport)
                    matchedReportIds << radiologyReport.messageControlId
                }
            }
        }
    }

    abstract void includeReport(RadiologyReport radiologyReport)

}
