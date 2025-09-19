package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.v281.segment.OBX
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.LineWrapper
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class ObxGenerator extends SegmentGenerator<OBX> {

    String setId
    Obx5Provider content
    String observationId
    String observationIdSuffix
    String observationSubId
    boolean includeTechnician = true
    public static final String IMPRESSION_SUFFIX = 'IMP'

    ObxGenerator() {

    }

    static List<ObxGenerator> forAddendum(String content, RadiologyReport radiologyReport) {
        forKnownSection(radiologyReport.getObservationIdSuffixForAddendum(), content, radiologyReport)
    }

    static List<ObxGenerator> forGeneralDescription(String content, RadiologyReport radiologyReport) {
        forKnownSection('GDT', content, radiologyReport)
    }

    static List<ObxGenerator> forImpression(String content, RadiologyReport radiologyReport) {
        forKnownSection(IMPRESSION_SUFFIX, content, radiologyReport)
    }

    static List<ObxGenerator> forTechnicianNote(String content, RadiologyReport radiologyReport) {
        forKnownSection('TCM', content, radiologyReport)
    }

    static List<ObxGenerator> forKnownSection(String sectionSuffix, String content, RadiologyReport radiologyReport) {
        LineWrapper.splitLongLines(content).collect { line ->
            radiologyReport.getBaseObxGenerator(line).observationIdSuffix(sectionSuffix)
        }
    }

    @Override
    void generateSegment(RadiologyReport radReport, OBX baseSegment) {
        baseSegment.getObx1_SetIDOBX().setValue(setId)
        baseSegment.getObx2_ValueType().setValue(content.getValueType(baseSegment))
        baseSegment.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue(observationId)
        Terser.set(baseSegment, 3, 0, 1, 2, observationIdSuffix)
        baseSegment.getObx4_ObservationSubID().setValue(observationSubId)
        content.encodeObx5(baseSegment)
        baseSegment.getObx11_ObservationResultStatus().setValue(getEncodedStatus(radReport.orcStatus))
        if (includeTechnician && radReport.technician != null) {
            encodeResponsibleObserver(radReport.technician, baseSegment)
        }
    }

    String getEncodedStatus(ReportStatus status) {
        status.currentText
    }

    protected void encodeResponsibleObserver(Person technician, OBX baseSegment) {
        technician.toXcn(baseSegment.getObx16_ResponsibleObserver(0), true)
    }

}
