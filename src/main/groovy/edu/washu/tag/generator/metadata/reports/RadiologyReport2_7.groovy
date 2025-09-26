package edu.washu.tag.generator.metadata.reports

import ca.uhn.hl7v2.HapiContext
import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.v281.datatype.ST
import ca.uhn.hl7v2.model.v281.group.ORU_R01_PATIENT
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.model.v281.segment.MSH
import ca.uhn.hl7v2.model.v281.segment.OBX
import edu.washu.tag.generator.ai.catalog.attribute.WithDiagnosisCodes
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder2_7
import edu.washu.tag.generator.hl7.v2.segment.*
import edu.washu.tag.generator.metadata.RadiologyReport

class RadiologyReport2_7 extends RadiologyReport {

    private static final DoctorEncoder doctorEncoder = new DoctorEncoder2_7()
    public static final ReportVersion CURRENT_VERSION = ReportVersion.V2_7

    @Override
    protected void createReport(HapiContext hapiContext, ORU_R01 radReport) {
        final MSH msh = radReport.getMSH()
        new MshGenerator().generateSegment(this, msh)

        final ORU_R01_PATIENT patientObj = radReport.PATIENT_RESULT.PATIENT
        getPidGenerator().generateSegment(this, patientObj.PID)
        getPv1Generator().generateSegment(this, patientObj.VISIT.PV1)

        getOrcGenerator()?.generateSegment(
            this,
            radReport.PATIENT_RESULT.ORDER_OBSERVATION.COMMON_ORDER.ORC
        )

        getObrGenerator().generateSegment(this, radReport.PATIENT_RESULT.ORDER_OBSERVATION.OBR)

        if (includeZpfAndZds()) {
            new ZpfGenerator().generateNonstandardSegment(this, radReport)
        }

        if (hl7Version == ReportVersion.V2_7 && generatedReport instanceof WithDiagnosisCodes) {
            generatedReport.parsedCodes.eachWithIndex { diagnosis, index ->
                new Dg1Generator(index, diagnosis, generatedReport.designator).generateNonstandardSegment(this, radReport)
            }
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

    @Override
    DoctorEncoder getDoctorEncoder() {
        doctorEncoder
    }

    @Override
    ObxGenerator getBaseObxGenerator(String content) {
        new ObxGenerator()
            .content(new SingleValueObx5Provider(content) {
                @Override
                Type resolveType(OBX baseSegment) {
                    final ST st = new ST(baseSegment.getMessage())
                    st.setValue(content)
                    st
                }
            })
    }

    @Override
    String getObservationIdSuffixForAddendum() {
        'ADT'
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

    protected OrcGenerator getOrcGenerator() {
        new OrcGenerator()
    }

    protected boolean includeZpfAndZds() {
        true
    }

}
