package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.OpenAiWrapper
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import io.temporal.activity.Activity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CyclicVariedGptGenerator extends CyclicVariedGenerator {

    String endpoint
    String apiKeyEnvVar
    String model
    int batchSize = 30
    private OpenAiWrapper openAiWrapper
    private static final Logger logger = LoggerFactory.getLogger(CyclicVariedGptGenerator)

    @Override
    protected List<PatientOutput> formBaseReports(List<Patient> patients, boolean temporalHeartbeat) {
        if (openAiWrapper == null) {
            openAiWrapper = new OpenAiWrapper(endpoint, apiKeyEnvVar, model)
        }
        final List<PatientOutput> patientOutputs = []
        while (patients.any { !it.reportsMatched }) {
            final List<Patient> sublist = prepareSublist(patients)
            final List<PatientOutput> generated = openAiWrapper.generateReportsForPatients(sublist)
            if (temporalHeartbeat) {
                Activity.executionContext.heartbeat('LLM called')
            }
            generated.each { patientOutput ->
                final Patient patient = sublist.find { pat ->
                    pat.patientIds[0].idNumber == patientOutput.patientId
                }
                if (patient != null) {
                    if (validateReportMatch(patient, patientOutput)) {
                        patient.setReportsMatched(true)
                        logger.info("Successfully matched output for patient ${patientOutput.patientId}")
                        patientOutputs << patientOutput
                    }
                }
            }
        }
        patientOutputs
    }

    private List<Patient> prepareSublist(List<Patient> patients) {
        final List<Patient> subset = []
        for (Patient patient : patients) {
            if (!patient.reportsMatched) {
                subset << patient
            }
            if (subset.size() == batchSize) {
                return subset
            }
        }
        subset
    }

    private static boolean validateReportMatch(Patient patient, PatientOutput patientOutput) {
        for (Study study : patient.studies) {
            final GeneratedReport generatedReport = patientOutput.generatedReports.find { report ->
                report.uid == study.studyInstanceUid
            }
            if (generatedReport == null) {
                return false
            }
        }
        return true
    }

}
