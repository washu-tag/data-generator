package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.*
import edu.washu.tag.generator.ai.catalog.builder.HistoricalReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
/**
 * Liver imaging report following LI-RADS
 */
abstract class LiRadsReport extends GeneratedReport<LiRadsReport> implements
    WithExamination,
    WithHistory,
    WithTechnique,
    WithComparison,
    WithFindings,
    WithImpression {

    @JsonPropertyDescription('Overall LI-RADS category for the exam')
    String category

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_4, ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        'This report follows the ACR Liver Imaging Reporting and Data System (LI-RADS) for a patient at high risk for hepatocellular carcinoma, ' +
            'such as a patient with cirrhosis or chronic hepatitis B, and the history section should reflect that. Be brief in the examination section. ' +
            'The findings should be significantly detailed, describing the liver parenchyma and contour, hepatic and portal vasculature, biliary tree, spleen, and ascites. '
    }

    @Override
    HistoricalReportTextBuilder writeReportText2_4(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final HistoricalReportTextBuilder textBuilder = new HistoricalReportTextBuilder(radiologyReport, this)
        addHistory(textBuilder)
        addComparison(textBuilder)
        addTechnique(textBuilder)
        addFindings(textBuilder)
        addImpression(textBuilder)
        addBoilerplate(textBuilder)
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder textBuilder = new ModernReportTextBuilder(radiologyReport)
        addExamination(textBuilder)
        addHistory(textBuilder)
        addTechnique(textBuilder)
        addComparison(textBuilder)
        addFindings(textBuilder)
        addImpression(textBuilder)
        addBoilerplate(textBuilder)
    }

    protected <S extends ReportTextBuilder<?, S>> S addBoilerplate(S textBuilder) {
        textBuilder
    }

}
