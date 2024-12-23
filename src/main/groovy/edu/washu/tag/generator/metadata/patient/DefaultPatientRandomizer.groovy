package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.enums.Nationality

class DefaultPatientRandomizer extends PatientRandomizer {

    @Override
    void randomize(Patient patient) {
        assignRandomRace(patient)
        assignRandomPersonName(patient, Nationality.AMERICAN)
    }

}
