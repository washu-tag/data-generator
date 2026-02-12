package edu.washu.tag.generator.query

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.QuerySourceDataProcessor
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.ai.catalog.attribute.WithDiagnosisCodes
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.validation.ExactRowsResult
import edu.washu.tag.validation.ExpectedQueryResult

import java.util.function.Predicate

class SingleReportDiagnosesQueryProcessor implements
    QuerySourceDataProcessor<BatchSpecification>,
    WithColumnExtractions<SingleReportDiagnosesQueryProcessor>,
    WithMatchedReportIds {

    ExactRowsResult expectation = new ExactRowsResult(uniqueIdColumnName: QueryUtils.COLUMN_DIAGNOSIS_CODE)

    @JsonIgnore
    Predicate<RadiologyReport> inclusionCriteria

    SingleReportDiagnosesQueryProcessor(Predicate<RadiologyReport> matchCriteria) {
        inclusionCriteria = matchCriteria
    }

    @Override
    void process(BatchSpecification batchSpecification) {
        for (Patient patient :batchSpecification.patients) {
            for (Study study : patient.studies) {
                final RadiologyReport radiologyReport = study.radReport
                radiologyReport.setPatient(patient)
                radiologyReport.setStudy(study)
                if (inclusionCriteria.test(radiologyReport)) {
                    includeReport(radiologyReport)
                    matchedReportIds << radiologyReport.messageControlId
                    return
                }
            }
        }
    }

    void includeReport(RadiologyReport radiologyReport) {
        final WithDiagnosisCodes dxReport = radiologyReport.generatedReport as WithDiagnosisCodes
        if (dxReport.diagnoses != null) {
            dxReport.parsedCodes.each { parsedCode ->
                final Map<String, String> assertions = columnExtractions.apply(radiologyReport)
                assertions.put('diagnosis_code_text', CodeCache.lookupCode(dxReport.designator, parsedCode))
                assertions.put('diagnosis_code_coding_system', dxReport.designator.hl7Representation)
                expectation.rowAssertions.put(
                    parsedCode, // this here is why this class can only support 1 report
                    assertions
                )
            }
        }
    }

    @Override
    ExpectedQueryResult outputExpectation() {
        expectation.setColumnTypes(columnTypes)
        expectation
    }

}
