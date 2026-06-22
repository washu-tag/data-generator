package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.ai.catalog.attribute.WithDiagnosisCodes
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.ExpectedMultipleRadReportQueryProcessor

import static edu.washu.tag.generator.hl7.v2.ReportVersion.V2_7
import static edu.washu.tag.generator.query.QueryUtils.*

class DiagnosisCount extends TestQuery<BatchSpecification> {

    DiagnosisCount() {
        super('dx_count', "SELECT * FROM ${TABLE_NAME}${SUFFIX_DIAGNOSES}")
        CodeCache.initializeCache()
        withDataProcessor(
            new ExpectedMultipleRadReportQueryProcessor() {
                @Override
                int countContribution(RadiologyReport radiologyReport) {
                    final GeneratedReport generatedReport = radiologyReport.generatedReport
                    (radiologyReport.hl7Version == V2_7 && generatedReport instanceof WithDiagnosisCodes) ? generatedReport.parsedCodes.size() : 0
                }
            }
        )
    }

}
