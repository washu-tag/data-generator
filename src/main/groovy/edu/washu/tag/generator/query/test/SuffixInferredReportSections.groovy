package edu.washu.tag.generator.query.test

import ca.uhn.hl7v2.model.v281.segment.OBX
import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.ai.catalog.AddendedReport
import edu.washu.tag.generator.ai.catalog.TechnicianNoteReport
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.FirstMatchingReportsRadReportResult

import java.util.function.Predicate

import static edu.washu.tag.generator.hl7.v2.ReportVersion.*
import static edu.washu.tag.generator.query.QueryUtils.reportOfClassWithContentAndVersion
import static edu.washu.tag.generator.query.QueryUtils.setSqlFindMessageControlIds

class SuffixInferredReportSections extends TestQuery<BatchSpecification> {

    SuffixInferredReportSections() {
        super('report_sections', null)
        withDataProcessor(
            new FirstMatchingReportsRadReportResult(
                1,
                [
                    reportOfClassWithContentAndVersion(AddendedReport, V2_3),
                    reportOfClassWithContentAndVersion(AddendedReport, V2_4),
                    reportOfClassWithContentAndVersion(AddendedReport, V2_7),
                    reportOfClassWithContentAndVersion(TechnicianNoteReport, V2_7)
                ]
            ).withColumnExtractions({ RadiologyReport radiologyReport ->
                [
                    'version_id': radiologyReport.hl7Version.hl7Version,
                    'report_section_addendum': radiologyReport.getReportTextForQueryExport(matchingObservationIdSuffix(['ADN', 'ADT'])),
                    'report_section_findings': radiologyReport.getReportTextForQueryExport(matchingObservationIdSuffix(['GDT'])),
                    'report_section_impression': radiologyReport.getReportTextForQueryExport(matchingObservationIdSuffix(['IMP'])),
                    'report_section_technician_note': radiologyReport.getReportTextForQueryExport(matchingObservationIdSuffix(['TCM']))
                ]
            })
        ).withPostProcessing({ query ->
            setSqlFindMessageControlIds(query)
        })
    }

    private Predicate<OBX> matchingObservationIdSuffix(List<String> possibleSuffixes) {
        { OBX obx ->
            final List<String> observationIdSubcomponents = (obx.getObx3_ObservationIdentifier().encode() ?: '').split('&')
            observationIdSubcomponents.size() > 1 && observationIdSubcomponents[1] in possibleSuffixes
        }
    }

}
