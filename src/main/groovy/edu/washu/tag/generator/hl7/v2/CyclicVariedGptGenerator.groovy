package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.OpenAiWrapper
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.ai.catalog.ReportRegistry
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import io.temporal.activity.Activity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

class CyclicVariedGptGenerator extends CyclicVariedGenerator {

    String endpoint
    String apiKeyEnvVar
    String model
    int batchSize = 30
    Double customReportFraction = null
    private OpenAiWrapper openAiWrapper
    private static final Logger logger = LoggerFactory.getLogger(CyclicVariedGptGenerator)

    @Override
    protected List<PatientOutput> formBaseReports(List<Patient> patients, boolean temporalHeartbeat) {
        if (openAiWrapper == null) {
            openAiWrapper = new OpenAiWrapper(endpoint, apiKeyEnvVar, model)
        }
        List<Patient> bulkPatients
        List<Patient> customPatients = []
        if (customReportFraction == null) {
            bulkPatients = patients
        } else {
            (bulkPatients, customPatients) = patients.split {
                ThreadLocalRandom.current().nextDouble() > customReportFraction
            }
        }

        final List<PatientOutput> patientOutputs = []
        while (bulkPatients.any { !it.reportsMatched }) {
            final List<Patient> sublist = prepareSublist(bulkPatients)
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
        customPatients.each { patient ->
            patientOutputs << openAiWrapper.generateReportsForPatient(
                patient,
                patient.studies.collectEntries { study ->
                    [(study): ReportRegistry.randomReportClass(study)]
                }
            )
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
