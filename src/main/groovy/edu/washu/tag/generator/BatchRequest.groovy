package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.util.SequentialIdGenerator
import io.temporal.activity.Activity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BatchRequest {

    int id
    int numPatients
    int numStudies
    int numSeries
    int patientOffset
    int studyOffset
    String temporalSummary
    String cohortName
    private static final Logger logger = LoggerFactory.getLogger(BatchRequest)

    void generateBatchWithHandler(BatchRequestContext batchRequestContext) {
        final SpecificationParameters specificationParameters = batchRequestContext.specificationParameters
        final SpecializedCohort cohort = lookupCohort(specificationParameters)
        specificationParameters.postprocess()
        batchRequestContext.generationCache.cache()

        final GenerationContext generationContext = new GenerationContext(
            specificationParameters: specificationParameters,
            patientIdGenerators: batchRequestContext.idOffsets.getPatientIdGeneratorsFromOffset(patientOffset),
            studyCountOverride: 0
        )
        int generatedStudies = 0
        int generatedSeries = 0
        final SequentialIdGenerator studyIdGenerator = batchRequestContext.idOffsets.getStudyIdEncoderFromOffset(studyOffset)
        final List<Patient> patients = []

        if (cohort == null) {
            numPatients.times { generatedPatients ->
                final Closure<Integer> remainingStudies = { numStudies - generatedStudies }

                generationContext.setCurrentAverageStudiesPerPatient(generatedPatients == 0 ? 0.0 : generatedStudies / generatedPatients)
                generationContext.setPreviouslyGeneratedSeries(generatedSeries)
                generationContext.setPreviouslyGeneratedStudies(generatedStudies)
                if (generatedPatients > numPatients - 4) {
                    generationContext.setStudyCountMaximum(remainingStudies() - (numPatients - generatedPatients - 1))
                } // 3rd to last must leave 2 reports for final 2 patients, 2nd to last must leave a report for final patient
                if (generatedPatients == numPatients - 1) { // for last patient in batch, we need to ensure exact number of studies
                    generationContext.setStudyCountOverride(remainingStudies())
                }
                final Patient patient = batchRequestContext.patientRandomizers.sample().createPatient(specificationParameters)
                patient.randomize(generationContext, studyIdGenerator)
                patient.studies.each { study ->
                    generatedStudies++
                    generatedSeries += study.series.size()
                    if (!specificationParameters.generateRadiologyReports) {
                        study.cachePatientIdsForStudy()
                    }
                }
                if (batchRequestContext.temporalHeartbeat) {
                    Activity.executionContext.heartbeat("Batch ${id}, patient ${generatedPatients + 1} post-DICOM")
                }
                patients << patient
            }

            logger.info("DICOM specs for batch ${id} with ${patients.size()} patients and ${patients*.studies*.size().sum()} studies")

            if (specificationParameters.generateRadiologyReports && batchRequestContext.generateReports) {
                specificationParameters.reportGeneratorImplementation.generateReportsForPatients(patients, batchRequestContext.temporalHeartbeat)
            }
        } else {
            numPatients.times { generatedPatients ->
                final Patient patient = batchRequestContext.patientRandomizers.sample().createPatient(specificationParameters)
                patient.randomize(generationContext, studyIdGenerator, cohort)
                patient.studies.each { study ->
                    if (!specificationParameters.generateRadiologyReports) {
                        study.cachePatientIdsForStudy()
                    }
                }
                if (batchRequestContext.temporalHeartbeat) {
                    Activity.executionContext.heartbeat("Cohort batch ${id}, patient ${generatedPatients + 1} post-DICOM")
                }
                patients << patient
            }

            logger.info("DICOM specs for cohort batch ${id} with ${patients.size()} patients and ${patients*.studies*.size().sum()} studies")

            if (specificationParameters.generateRadiologyReports && batchRequestContext.generateReports) {
                specificationParameters.reportGeneratorImplementation.generateReportsForPatients(patients, batchRequestContext.temporalHeartbeat)
            }
        }

        patients.eachWithIndex { patient, generatedPatients ->
            batchRequestContext.handler.accept(patient)
            if ((generatedPatients + 1) % 100 == 0) {
                logger.info("Generated DICOM and/or HL7 for patient ${patient.patientInstanceUid} in batch ${id} [${generatedPatients + 1}/${numPatients}]")
            }
            if (batchRequestContext.temporalHeartbeat) {
                Activity.executionContext.heartbeat("Batch ${id}, patient ${generatedPatients + 1}")
            }
        }
    }

    private SpecializedCohort lookupCohort(SpecificationParameters specificationParameters) {
        if (cohortName == null) {
            null
        } else {
            final SpecializedCohort cohort = specificationParameters.cohorts.find { it.name == cohortName }
            if (cohort == null) {
                throw new IllegalStateException("Could not find cohort with name ${cohortName}")
            }
            cohort
        }
    }

}
