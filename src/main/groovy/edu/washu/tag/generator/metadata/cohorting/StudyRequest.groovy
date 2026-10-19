package edu.washu.tag.generator.metadata.cohorting

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.catalog.ReportRegistry
import edu.washu.tag.generator.metadata.Diagnosis
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.Study

class StudyRequest implements TrajectoryStep {

    String name
    Protocol protocol
    List<String> diagnosisCodes = []
    Map<String, Integer> diagnosisCodesWeighted = [:]
    String additionalGenerationContext = ''
    CodeStrategy codeStrategy = CodeStrategy.ONLY_ONE_FIXED_FOR_PATIENT
    int studyOffsetMin
    int studyOffsetMax
    List<String> reportClasses = [] // simple names of report classes to choose from; empty means any applicable class

    @JsonIgnore
    @Override
    List<TrajectoryBranch> expand(List<TrajectoryBranch> branches) {
        branches.collect { b -> new TrajectoryBranch(name: b.name, numPatients: b.numPatients, steps: b.steps + this) }
    }

    @JsonIgnore
    List<Diagnosis> randomizeCodes(Study study) {
        final Map<String, Integer> combinedCodes = new HashMap<>(diagnosisCodesWeighted)
        diagnosisCodes.each { code ->
            combinedCodes.put(code, 10)
        }
        codeStrategy.randomizeAndActualizeCodes(study, combinedCodes)
    }

    @JsonIgnore
    List<Class<? extends GeneratedReport>> resolveReportClasses() {
        reportClasses.collect { ReportRegistry.lookupReportClass(it) }
    }

}
