package edu.washu.tag.generator.metadata.study

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.util.RandomGenUtils

import java.time.LocalDate

class UniformStudyDateDistribution implements StudyDateDistribution {

    LocalDate minDate = Patient.imagingDataEpoch
    LocalDate maxDate = LocalDate.now().minusMonths(6)

    @Override
    LocalDate generateStudyDate(Patient patient) {
        RandomGenUtils.randomDate(
            [patient.earliestAvailableStudyDate, minDate].max(),
            maxDate
        )
    }

}
