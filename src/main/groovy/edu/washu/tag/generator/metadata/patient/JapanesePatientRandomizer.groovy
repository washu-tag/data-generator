package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.enums.Nationality

class JapanesePatientRandomizer extends PatientRandomizer {

    @Override
    void randomize(Patient patient) {
        assignRandomPersonName(patient, Nationality.JAPANESE)
    }

}
