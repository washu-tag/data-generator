package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.metadata.Patient

abstract class ReportGenerator {

    abstract void generateReportsForPatient(Patient patient)

}
