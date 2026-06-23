package edu.washu.tag.generator.metadata.study

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.util.RandomGenUtils

import java.time.LocalDate

class UniformStudyDateDistribution extends StudyDateDistribution {

    @Override
    LocalDate generateStudyDate(Patient patient) {
        RandomGenUtils.randomDate(
            [patient.earliestAvailableStudyDate, minDate].max(),
            maxDate
        )
    }

}
