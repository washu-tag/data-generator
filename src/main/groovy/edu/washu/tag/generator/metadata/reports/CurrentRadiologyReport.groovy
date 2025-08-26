package edu.washu.tag.generator.metadata.reports

import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.v281.group.ORU_R01_PATIENT
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.model.v281.segment.MSH
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.*
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.util.LineWrapper

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
class CurrentRadiologyReport extends RadiologyReport {

    public static final ReportVersion CURRENT_VERSION = ReportVersion.V2_7

    @Override
    protected void createReport(HapiContext hapiContext, ORU_R01 radReport) {
        final MSH msh = radReport.getMSH()
        new MshGenerator().generateSegment(this, msh)

        final ORU_R01_PATIENT patientObj = radReport.PATIENT_RESULT.PATIENT
        getPidGenerator().generateSegment(this, patientObj.PID)
        getPv1Generator().generateSegment(this, patientObj.VISIT.PV1)

        if (includeOrc()) {
            new OrcGenerator().generateSegment(
                    this,
                    radReport.PATIENT_RESULT.ORDER_OBSERVATION.COMMON_ORDER.ORC
            )
        }

        getObrGenerator().generateSegment(this, radReport.PATIENT_RESULT.ORDER_OBSERVATION.OBR)


        if (includeZpfAndZds()) {
            new ZpfGenerator().generateNonstandardSegment(this, radReport)
        }

        if (includeObx) {
            generatedReport.addObx(radReport, this, getHl7Version())
        }

        if (includeZpfAndZds()) {
            new ZdsGenerator().generateNonstandardSegment(this, radReport)
        }

        radReport
    }

    @Override
    ReportVersion getHl7Version() {
        CURRENT_VERSION
    }

    protected PidGenerator getPidGenerator() {
        new PidGenerator()
    }

    protected Pv1Generator getPv1Generator() {
        new Pv1Generator()
    }

    protected ObrGenerator getObrGenerator() {
        new ObrGenerator()
    }

    protected boolean includeOrc() {
        true
    }

    protected boolean includeZpfAndZds() {
        true
    }

}
