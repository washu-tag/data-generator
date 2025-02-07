package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.util.FileIOUtils
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class ProcedureCode {

    String impliedModality
    CodedTriplet codedTriplet
    String id
    private static final Map<String, ProcedureCode> codes = readCodes()

    static ProcedureCode lookup(String codeId) {
        codes.get(codeId)
    }

    private static Map<String, ProcedureCode> readCodes() {
        final Map<String, ProcedureCode> codes = new ObjectMapper()
            .readValue(FileIOUtils.readResource('procedure_codes.json'), new TypeReference<Map<String, ProcedureCode>>() {})
        codes.each { id, code ->
            code.setId(id)
        }
        codes
    }

}
