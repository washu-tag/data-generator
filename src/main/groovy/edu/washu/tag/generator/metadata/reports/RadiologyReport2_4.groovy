package edu.washu.tag.generator.metadata.reports

import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.parser.PipeParser
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoderLegacy
import edu.washu.tag.generator.hl7.v2.segment.*

class RadiologyReport2_4 extends RadiologyReport2_7 {

    private static final DoctorEncoder doctorEncoder = new DoctorEncoderLegacy()
    private static final Parser cachedParser = cacheParser()

    @Override
    ReportVersion getHl7Version() {
        ReportVersion.V2_4
    }

    @Override
    DoctorEncoder getDoctorEncoder() {
        doctorEncoder
    }

    @Override
    protected PidGenerator getPidGenerator() {
        new PidGenerator2_4()
    }

    @Override
    protected Pv1Generator getPv1Generator() {
        new Pv1Generator2_4()
    }

    @Override
    protected ObrGenerator getObrGenerator() {
        new ObrGenerator2_4()
    }

    @Override
    protected OrcGenerator getOrcGenerator() {
        null
    }

    @Override
    protected boolean includeZpfAndZds() {
        false
    }

    @Override
    void postProcess() {
        super.postProcess()
        setParser(cachedParser)
    }

    private static Parser cacheParser() {
        final Parser pipeParser = new PipeParser()
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/VISIT/PV1-8-3')
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/VISIT/PV1-9-3')
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/ORDER_OBSERVATION/OBR-33-3')
        pipeParser
    }

}
