package edu.washu.tag.generator.metadata.cohorting

import edu.washu.tag.generator.metadata.Protocol

class StudyRequest {

    String name
    Protocol protocol
    List<String> diagnosisCodes
    String additionalGenerationContext = ''
    CodeStrategy codeStrategy = CodeStrategy.ONLY_ONE
    int studyOffsetMin
    int studyOffsetMax

}
