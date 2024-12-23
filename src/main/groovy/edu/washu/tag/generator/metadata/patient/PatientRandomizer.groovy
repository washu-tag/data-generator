package edu.washu.tag.generator.metadata.patient

import org.apache.commons.math3.distribution.BetaDistribution
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.distribution.RealDistribution
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.module.patient.PatientModule
import edu.washu.tag.generator.util.RandomGenUtils

abstract class PatientRandomizer {

    public static final EnumeratedDistribution<Race> raceDistribution = raceRandomizer()
    public static final RealDistribution heightModDistribution = new NormalDistribution(0, 1)
    public static final RealDistribution weightModDistribution = new BetaDistribution(2, 5)

    Patient createPatient(SpecificationParameters specificationParameters) {
        final Patient patient = new Patient()
        new PatientModule().apply(specificationParameters, patient)
        randomize(patient)
        assignRandomPhysique(patient)
        patient
    }

    abstract void randomize(Patient patient)

    void assignRandomPersonName(Patient patient, Nationality nationality) {
        patient.setPatientName(nationality.generateRandomPerson(patient.sex))
        patient.setNationality(nationality)
    }

    void assignRandomRace(Patient patient) {
        patient.setRace(raceDistribution.sample())
    }

    void assignRandomPhysique(Patient patient) {
        while (true) {
            final double sample = heightModDistribution.sample()
            if (Math.abs(sample) <= 3) {
                patient.setPersonalHeightMod(sample)
                break
            }
        }
        patient.setPersonalWeightMod(10 * weightModDistribution.sample() - 2) // apply affine transformation to coerce value from [0, 1] to [-2, 8]
    }

    private static EnumeratedDistribution<Race> raceRandomizer() {
        RandomGenUtils.setupWeightedLottery([
                (Race.WHITE): 50,
                (Race.BLACK): 25,
                (Race.ASIAN): 15,
                (Race.OTHER): 5,
                (Race.PACIFIC_ISLANDER): 3,
                (Race.AMERICAN_INDIAN): 2
        ])
    }

}