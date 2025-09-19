package edu.washu.tag.generator.metadata.reports

import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.v281.datatype.FT
import ca.uhn.hl7v2.model.v281.segment.OBX
import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.parser.PipeParser
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoder
import edu.washu.tag.generator.hl7.v2.model.DoctorEncoderLegacy
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator
import edu.washu.tag.generator.hl7.v2.segment.ObrGenerator2_3
import edu.washu.tag.generator.hl7.v2.segment.ObxGenerator
import edu.washu.tag.generator.hl7.v2.segment.OrcGenerator
import edu.washu.tag.generator.hl7.v2.segment.OrcGenerator2_3
import edu.washu.tag.generator.hl7.v2.segment.PidGenerator
import edu.washu.tag.generator.hl7.v2.segment.PidGenerator2_3
import edu.washu.tag.generator.hl7.v2.segment.Pv1Generator
import edu.washu.tag.generator.hl7.v2.segment.Pv1Generator2_3
import edu.washu.tag.generator.hl7.v2.segment.SingleValueObx5Provider
import edu.washu.tag.generator.metadata.ProcedureCode

class RadiologyReport2_3 extends RadiologyReport2_7 {

    private static final DoctorEncoder doctorEncoder = new DoctorEncoderLegacy()
    private static final Parser cachedParser = cacheParser()

    @Override
    ReportVersion getHl7Version() {
        ReportVersion.V2_3
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
                    final FT ft = new FT(baseSegment.getMessage())
                    ft.setValue(content)
                    ft
                }
            }).observationId(ProcedureCode.lookup(study.procedureCodeId).codedTriplet.codeValue)
            .includeTechnician(false)
    }

    @Override
    String getObservationIdSuffixForAddendum() {
        'ADN'
    }

    @Override
    protected PidGenerator getPidGenerator() {
        new PidGenerator2_3()
    }

    @Override
    protected Pv1Generator getPv1Generator() {
        new Pv1Generator2_3()
    }

    @Override
    protected OrcGenerator getOrcGenerator() {
        new OrcGenerator2_3()
    }

    @Override
    protected ObrGenerator getObrGenerator() {
        new ObrGenerator2_3()
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
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/PID-5-6')
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/PID-11-6')
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/PATIENT/VISIT/PV1-7-7') // see Pv1Generator2_3
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/ORDER_OBSERVATION/COMMON_ORDER/ORC-10-7') // see OrcGenerator2_3
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/ORDER_OBSERVATION/OBR-34-7') // see ObrGenerator2_3
        pipeParser.getParserConfiguration().addForcedEncode('PATIENT_RESULT/ORDER_OBSERVATION/OBR-35-7') // see ObrGenerator2_3
        pipeParser
    }


}
