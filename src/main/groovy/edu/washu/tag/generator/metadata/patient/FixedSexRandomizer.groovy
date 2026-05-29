package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.enums.Sex

/**
 * Useful for scenarios such as generating a dataset of only mammograms
 */
class FixedSexRandomizer extends DefaultPatientRandomizer {

    Sex sex

    @Override
    void randomize(Patient patient) {
        patient.setSex(sex)
        super.randomize(patient)
    }

}
