package edu.washu.tag.generator.ai.catalog.builder

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.catalog.attribute.WithExamination
import edu.washu.tag.generator.hl7.v2.StudyReportGenerator2_4
import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.RadiologyReport

class HistoricalReportTextBuilder extends ReportTextBuilder<String, HistoricalReportTextBuilder> {

    private GeneratedReport generatedReport
    private static final String imagingTableHeaders = 'ACC#  Date Time  Exam\n\n'

    HistoricalReportTextBuilder(RadiologyReport radReport, GeneratedReport generatedReport) {
        super(radReport, { String input, RadiologyReport report -> [input] })
        this.generatedReport = generatedReport
        final String accessionNumber = radiologyReport.fillerOrderNumber.getEi1_EntityIdentifier().value
        final CodedTriplet procedureCode = ProcedureCode.lookup(radiologyReport.study.procedureCodeId).codedTriplet
        final String procedure = "${procedureCode.codeValue.replace('\\D', '')} ${procedureCode.codeMeaning}"
        add(imagingTableHeaders)

        final imagingDateTime = StudyReportGenerator2_4.DATE_TIME_FORMATTER.format(radiologyReport.reportDateTime)
        add("${accessionNumber} ${imagingDateTime} ${procedure}")

        if (generatedReport instanceof WithExamination) {
            generatedReport.addExamination(this)
        }
    }

    String compileText() {
        textGenerators.join('\n')
    }

}
