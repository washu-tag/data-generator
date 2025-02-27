package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.AzureWrapper
import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.metadata.Patient

class CyclicVariedGptGenerator extends CyclicVariedGenerator {

    @Override
    protected List<GeneratedReport> formBaseReports(Patient patient) {
        new AzureWrapper().generateReports(patient)
    }

}
