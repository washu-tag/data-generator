package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.OpenAiWrapper
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.ai.catalog.ReportRegistry
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import io.temporal.activity.Activity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

class CyclicVariedGptGenerator extends CyclicVariedGenerator {

    String endpoint
    String apiKeyEnvVar
    String model
    Map<String, List<String>> queryParams = [:]
    int batchSize = 30
    Double customReportFraction = null
    private OpenAiWrapper openAiWrapper
    private static final Logger logger = LoggerFactory.getLogger(CyclicVariedGptGenerator)

    @Override
    protected List<PatientOutput> formBaseReports(List<Patient> patients, boolean temporalHeartbeat) {
        if (openAiWrapper == null) {
            openAiWrapper = new OpenAiWrapper(endpoint, apiKeyEnvVar, model, queryParams)
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

        final Closure<Void> heartbeatAndLog = { String message ->
            if (temporalHeartbeat) {
                Activity.executionContext.heartbeat(message)
            }
            logger.info(message)
        }

        heartbeatAndLog("Generating ${patients.size()} patients with ${bulkPatients.size()} generated in bulk and ${customPatients.size()} generated with customized reports")

        final List<PatientOutput> patientOutputs = []
        while (bulkPatients.any { !it.reportsMatched }) {
            final List<Patient> sublist = prepareSublist(bulkPatients)
            final List<PatientOutput> generated = openAiWrapper.generateReportsForPatients(sublist)
            heartbeatAndLog('LLM called')
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
        customPatients.eachWithIndex { patient, index ->
            patientOutputs << openAiWrapper.generateReportsForPatient(
                patient,
                patient.studies.collectEntries { study ->
                    [(study): ReportRegistry.randomReportClass(study)]
                }
            )
            heartbeatAndLog("Generated customized reports for patient [${index + 1}/${customPatients.size()}]")
        }
        patientOutputs
    }

    @Override
    protected overwriteReport(RadiologyReport reportToOverwrite, Class<? extends GeneratedReport> reportClass) {
        reportToOverwrite.setGeneratedReport(
            openAiWrapper.generateReportsForPatient(
                reportToOverwrite.study.patient,
                [(reportToOverwrite.study): reportClass]
            ).generatedReports[0]
        )
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
            if (!generatedReport.validateReport()) {
                return false
            }
        }
        return true
    }

}
