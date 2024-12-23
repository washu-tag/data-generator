package edu.washu.tag.generator.metadata.module.study

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.metadata.module.StudyLevelModule
import edu.washu.tag.generator.util.RandomGenUtils

import java.time.Period
import java.util.concurrent.ThreadLocalRandom

class PatientStudyModule implements StudyLevelModule {

    private static final PolynomialSplineFunction maleReferenceHeightInterpolator = new SplineInterpolator().interpolate(
            [16, 17, 18, 19, 25, 35, 45, 55, 65, 75, 85, 95, 105] as double[],
            [1.739, 1.749, 1.756, 1.763, 1.764, 1.766, 1.762, 1.760, 1.753, 1.730, 1.72, 1.715, 1.70] as double[]
    ) // last three points are made up
    private static final PolynomialSplineFunction femaleReferenceHeightInterpolator = new SplineInterpolator().interpolate(
            [16, 17, 18, 19, 25, 35, 45, 55, 65, 75, 85, 95, 105] as double[],
            [1.623, 1.625, 1.617, 1.630, 1.629, 1.634, 1.629, 1.619, 1.605, 1.593, 1.573, 1.555, 1.521] as double[]
    ) // last three points are made up
    private static final Closure<Double> weightInterpolator = { double height -> 96 * height - 82 }
    private static final double HEIGHT_STANDARD_DEVIATION_ESTIMATE = 0.05
    private static final double WEIGHT_MOD_WEIGHTING_CONSTANT = 10

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study) {
        if (RandomGenUtils.weightedCoinFlip(specificationParameters.patientAgeEncodingPercent)) {
            study.setPatientAge(Period.between(patient.dateOfBirth, study.studyDate).years.toString().padLeft(3, '0') + 'Y')
        }

        if (study.protocol.allowPatientPhysiqueEncoding()) {
            final double referenceHeight = (patient.sex == Sex.MALE ? maleReferenceHeightInterpolator : femaleReferenceHeightInterpolator).value(Period.between(patient.dateOfBirth, study.studyDate).years)
            final double heightMod = patient.personalHeightMod + ThreadLocalRandom.current().nextDouble(-0.5, 0.5)
            final double height = referenceHeight + HEIGHT_STANDARD_DEVIATION_ESTIMATE * heightMod
            if (RandomGenUtils.weightedCoinFlip(specificationParameters.patientSizeEncodingPercent)) {
                if (RandomGenUtils.weightedCoinFlip(specificationParameters.patientSizeEncodedAsZeroPercent)) {
                    study.setPatientSize(0.0)
                } else {
                    study.setPatientSize(height.round(2))
                }
            }
            if (RandomGenUtils.weightedCoinFlip(specificationParameters.patientWeightEncodingPercent)) {
                if (RandomGenUtils.weightedCoinFlip(specificationParameters.patientWeightEncodedAsZeroPercent)) {
                    study.setPatientWeight(0.0)
                } else {
                    final double referenceWeight = weightInterpolator(height)
                    final double weightMod = patient.personalWeightMod + ThreadLocalRandom.current().nextDouble(-0.5, 0.5)
                    study.setPatientWeight((referenceWeight + WEIGHT_MOD_WEIGHTING_CONSTANT * weightMod).round(2))
                }
            }
        }

        // TODO: Medical Alerts, Allergies, Smoking Status, Pregnancy Status, Patient State, Occupation
    }

}
