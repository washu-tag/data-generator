package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.ai.catalog.attribute.WithFindings
import edu.washu.tag.generator.ai.catalog.attribute.WithImpression
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.HistoricalReportStructurer
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObxManager
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.StringReplacements
import edu.washu.tag.util.FileIOUtils

class ClassicReport extends GeneratedReport<ClassicReport> implements
    WithExamination,
    WithImpression,
    WithFindings {

    private static final String BASE_REPORT_SKELETON_HISTORICAL = FileIOUtils.readResource('legacy_report_skeleton.txt')

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        'Be brief in the examination section. The findings and impressions should be significantly detailed.'
    }

    @Override
    String writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final String accessionNumber = radiologyReport.fillerOrderNumber.getEi1_EntityIdentifier().value
        final CodedTriplet procedureCode = ProcedureCode.lookup(radiologyReport.study.procedureCodeId).codedTriplet
        BASE_REPORT_SKELETON_HISTORICAL
            .replace(StringReplacements.ACCESSION_NUMBER_PLACEHOLDER, accessionNumber)
            .replace(StringReplacements.DATE_TIME_PLACEHOLDER, HistoricalReportStructurer.DATE_TIME_FORMATTER.format(radiologyReport.reportDateTime))
            .replace(StringReplacements.PROCEDURE, "${procedureCode.codeValue.replace('\\D', '')} ${procedureCode.codeMeaning}")
            .replace(StringReplacements.EXAMINATION_PLACEHOLDER, examination)
            .replace(StringReplacements.FINDINGS_PLACEHOLDER, findings)
            .replace(StringReplacements.IMPRESSION_PLACEHOLDER, impression)
    }

    @Override
    ObxManager writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final Person interpreter = radiologyReport.getEffectivePrincipalInterpreter()

        new ObxManager([
            ObxGenerator.forGeneralDescription("EXAMINATION: ${examination}\n"),
            ObxGenerator.forGeneralDescription("FINDINGS: \n${findings}\n"),
            ObxGenerator.forImpression("IMPRESSION: \n${impression}"),
            ObxGenerator.forImpression(
                "Dictated by: ${interpreter.givenNameAlphabetic} ${interpreter.familyNameAlphabetic} Interpreter, M.D."
            )
        ])
    }

}
