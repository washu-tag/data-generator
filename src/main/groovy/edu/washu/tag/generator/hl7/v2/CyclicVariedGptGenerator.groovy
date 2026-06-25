package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.ai.OpenAiWrapper
import edu.washu.tag.generator.ai.PatientOutput
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.ReportRegistry
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import edu.washu.tag.generator.ai.catalog.attribute.WithDiagnosisCodes
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
            final Runnable llmHeartbeat = temporalHeartbeat
                ? { Activity.executionContext.heartbeat('Calling LLM') } as Runnable
                : null
            openAiWrapper = new OpenAiWrapper(endpoint, apiKeyEnvVar, model, queryParams, llmHeartbeat)
        }
        final boolean isCohort = patients.any { it.parentCohort != null }
        List<Patient> bulkPatients = []
        List<Patient> customPatients = []
        if (customReportFraction == null) {
            bulkPatients = patients
        } else if (isCohort) { // for custom cohort, each patient is pre-assigned different diagnosis codes, don't want to overload model context with per-study complex specifications
            customPatients = patients
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
                    pat.epicMrn == patientOutput.patientId
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
        if (isCohort) {
            customPatients.eachWithIndex { patient, index ->
                patientOutputs << openAiWrapper.generateReportsForPatientFromPlaceholders(
                    patient,
                    patient.studies.collectEntries { study ->
                        final GeneratedReport placeholderReport = ReportRegistry.randomReportClass(study).getDeclaredConstructor().newInstance()
                        final String dxString = study.resolveDiagnoses().collect { it.code }.join(',')
                        if (placeholderReport instanceof WithDiagnosisCodes) {
                            placeholderReport.metaClass.diagnosisPrompt = {
                                "The diagnoses should be set to ${dxString} and the meaning of those codes used in the rest of the report. "
                            }
                            placeholderReport.designator = DiagnosisCodeDesignator.ICD_10
                        } else {
                            study.setAdditionalGenerationContext(
                                "The diagnoses for this report are ${dxString}. The meaning of those codes should be used in the rest of the report. ${study.additionalGenerationContext ?: ''} "
                            )
                        }
                        [(study): placeholderReport]
                    }
                )
                heartbeatAndLog("Generated customized reports for cohort patient [${index + 1}/${customPatients.size()}]")
            }
        } else {
            customPatients.eachWithIndex { patient, index ->
                patientOutputs << openAiWrapper.generateReportsForPatient(
                    patient,
                    patient.studies.collectEntries { study ->
                        [(study): ReportRegistry.randomReportClass(study)]
                    }
                )
                heartbeatAndLog("Generated customized reports for patient [${index + 1}/${customPatients.size()}]")
            }
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
