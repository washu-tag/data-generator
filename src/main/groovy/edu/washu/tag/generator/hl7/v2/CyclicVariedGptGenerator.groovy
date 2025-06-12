package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.OpenAiWrapper
import edu.washu.tag.generator.metadata.Patient

class CyclicVariedGptGenerator extends CyclicVariedGenerator {

    String endpoint
    String apiKeyEnvVar
    String model

    @Override
    protected List<GeneratedReport> formBaseReports(Patient patient) {
        new OpenAiWrapper(endpoint, apiKeyEnvVar, model).generateReports(patient)
    }

}
