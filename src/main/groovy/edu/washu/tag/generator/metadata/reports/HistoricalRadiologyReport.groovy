package edu.washu.tag.generator.metadata.reports

import ca.uhn.hl7v2.parser.Parser
import ca.uhn.hl7v2.parser.PipeParser
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.segment.*

class HistoricalRadiologyReport extends CurrentRadiologyReport {

    private static final Parser cachedParser = cacheParser()

    @Override
    ReportVersion getHl7Version() {
        ReportVersion.V2_4
    }

    @Override
    protected PidGenerator getPidGenerator() {
        new PidGeneratorHistorical()
    }

    @Override
    protected Pv1Generator getPv1Generator() {
        new Pv1GeneratorHistorical()
    }

    @Override
    protected ObrGenerator getObrGenerator() {
        new ObrGeneratorHistorical()
    }

    @Override
    protected boolean includeOrc() {
        false
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
