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
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.protocols.MammogramFourView

class TechnicianNoteReport extends GeneratedReport<TechnicianNoteReport> implements
    WithExamination,
    WithImpression,
    WithFindings {

    @JsonPropertyDescription('Note added to the report by the technician')
    String technicianNote

    @Override
    List<ReportVersion> supportedVersions() {
        [ReportVersion.V2_7]
    }

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        final List<String> examples = ['Shortness of breath.', 'Pt unable to hold still during scan']
        if (study.bodyPartExamined != null) {
            if (!(study.bodyPartExamined in [BodyPart.WHOLEBODY, BodyPart.SKULL, BodyPart.HEART, BodyPart.BRAIN])) {
                final String part = study.bodyPartExamined.codeMeaning
                examples << "${part} pain".toString()
                examples << "Pt noted pain in ${part}".toString()
            }
        }
        'Be brief in the examination section. The findings and impressions should be significantly detailed. ' +
            'The technicianNote section should not be empty and could be anything the technician needs to note in the report. It should be less formal in tone than the other sections.' +
            'It should focus on the examination itself, so it should not mention things such as image quality. Examples of possible values could be things from this non-exhaustive list:\n' +
            examples.collect { '* ' + it }.join('\n')
    }

    @Override
    ModernReportTextBuilder writeReportText2_7(ORU_R01 radReportMessage, RadiologyReport radiologyReport) {
        final ModernReportTextBuilder obxManager = new ModernReportTextBuilder(radiologyReport)
        addExamination(obxManager)
        addFindings(obxManager)
        addImpression(obxManager)
        obxManager.beginTechnicianNote()
        obxManager.add(technicianNote)
    }

}
