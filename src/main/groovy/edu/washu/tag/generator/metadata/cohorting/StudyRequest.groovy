package edu.washu.tag.generator.metadata.cohorting

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.metadata.Diagnosis
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.Study

class StudyRequest {

    String name
    Protocol protocol
    List<String> diagnosisCodes = []
    Map<String, Integer> diagnosisCodesWeighted = [:]
    String additionalGenerationContext = ''
    CodeStrategy codeStrategy = CodeStrategy.ONLY_ONE_FIXED_FOR_PATIENT
    int studyOffsetMin
    int studyOffsetMax

    @JsonIgnore
    List<Diagnosis> randomizeCodes(Study study) {
        final Map<String, Integer> combinedCodes = new HashMap<>(diagnosisCodesWeighted)
        diagnosisCodes.each { code ->
            combinedCodes.put(code, 10)
        }
        codeStrategy.randomizeAndActualizeCodes(study, combinedCodes)
    }

}
