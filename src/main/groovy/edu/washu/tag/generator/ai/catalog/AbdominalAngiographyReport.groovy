package edu.washu.tag.generator.ai.catalog

import ca.uhn.hl7v2.model.v281.message.ORU_R01
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.ai.catalog.attribute.WithFindings
import edu.washu.tag.generator.ai.catalog.attribute.WithImpression
import edu.washu.tag.generator.ai.catalog.builder.ModernReportTextBuilder
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study

import java.util.concurrent.ThreadLocalRandom

class AbdominalAngiographyReport extends GeneratedReport<AbdominalAngiographyReport> implements
    WithExamination,
    WithFindings,
    WithImpression {

    @JsonPropertyDescription('Medical justification for the procedure including possibly some brief patient history')
    String clinicalIndication

    @JsonPropertyDescription('Medical description of the clinical procedure performed')
    String procedure

    @JsonPropertyDescription('Description of the anesthesia used including application site')
    String anesthesia

    @JsonPropertyDescription('Description of the medications used in the procedure, including anesthesia')
    String medications

    @JsonPropertyDescription('List of implements used in performing the procedure')
    List<String> materials

    @JsonPropertyDescription('Brief description of contrast agent used for the angiography procedure, including dosage')
    String contrast

    @JsonPropertyDescription('Highly detailed description of how the procedure was carried out')
    String technique

    @JsonPropertyDescription('Brief description of patient blood loss from the procedure, likely just None')
    String estimatedBloodLoss

    @JsonPropertyDescription('Unforeseen complications encountered in the procedure, possibly just None')
    String complications

    @JsonPropertyDescription('Description of follow-up activities after the procedure to help the patient')
    String plan

    Boolean materialsHeader = ThreadLocalRandom.current().nextBoolean()

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_7]
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder textBuilder = new ModernReportTextBuilder()
        addExamination(textBuilder)
        textBuilder.addSection('CLINICAL INDICATION', clinicalIndication)
        textBuilder.addSection('PROCEDURE', procedure)
        // TODO: physicians/surgeons
        textBuilder.addSection('ANESTHESIA', anesthesia)
        textBuilder.addSection('MEDICATIONS', medications)
        textBuilder.addSection(materialsHeader ? 'MATERIALS' : 'DEVICES', materials.join('\n'))
        textBuilder.addSection('CONTRAST', contrast)
        textBuilder.addSection('TECHNIQUE', technique)
        textBuilder.addSection('ESTIMATED BLOOD LOSS', estimatedBloodLoss)
        textBuilder.addSection('COMPLICATIONS', complications)
        addFindings(textBuilder)
        addImpression(textBuilder)
        textBuilder.addSection('PLAN', plan)
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        "Be brief in the examination section. The findings and impressions should be significantly detailed. The technique section should be extensive, preferably around 300 words."
    }

    @Override
    Boolean checkApplicability(Study study) {
        ObrGenerator.derivePrimaryImagingModality(study) == 'XA'
    }

}
