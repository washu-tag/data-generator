package edu.washu.tag.generator.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.ai.catalog.attribute.WithComparison
import edu.washu.tag.generator.ai.wrapper.ArrayWrapperLlmCall
import edu.washu.tag.generator.ai.wrapper.GenericLlmCall
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.TimeUtils
import edu.washu.tag.util.FileIOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpenAiWrapper {

    private final OpenAIClient client
    private final String model
    private static final Logger logger = LoggerFactory.getLogger(OpenAiWrapper)
    private static final String BASE_RAD_CONTEXT = FileIOUtils.readResource('rad_context.txt')
    private static final String BASE_RAD_CONTEXT_SINGULAR = FileIOUtils.readResource('rad_context_singular.txt')
    private static final String BASE_RAD_PROMPT = FileIOUtils.readResource('rad_prompt.txt')
    private static final String BASE_RAD_PROMPT_SINGULAR = FileIOUtils.readResource('rad_prompt_singular.txt')
    private static final ObjectMapper objectMapper = new ObjectMapper()

    OpenAiWrapper(String endpoint, String apiKeyEnvVar, String modelName, Map<String, List<String>> queryParams) {
        model = modelName
        client = OpenAIOkHttpClient.builder()
            .apiKey(System.getenv(apiKeyEnvVar))
            .baseUrl(endpoint)
            .queryParams(queryParams)
            .build()
    }

    PatientOutput generateReportsForPatient(Patient patient, Map<Study, Class<? extends GeneratedReport>> reports) {
        final List<String> uidMapping = []
        final PatientRep patientRep = convertToPatientRep(patient, uidMapping)
        final PatientOutput patientOutput = new PatientOutput(patientId: patient.patientIds[0].idNumber)

        patientRep.studies.each { studyRep ->
            final String studyInstanceUid = uidMapping[Integer.parseInt(studyRep.uid)]
            final Map.Entry<Study, Class<? extends GeneratedReport>> entry = reports.find { entry ->
                entry.key.studyInstanceUid == studyInstanceUid
            }
            final Study study = entry.key
            final Class<? extends GeneratedReport> reportClass = entry.value
            final GeneratedReport promptReport = reportClass.getDeclaredConstructor().newInstance()

            final String comparison = {
                if (promptReport instanceof WithComparison && studyRep.compareTo == null) {
                    'There is no previous known comparison, so the comparison property should contain only a brief mention of no comparison available. '
                } else {
                    ''
                }
            }()

            final String fullComparisonSerialization = {
                if (studyRep.compareTo == null) {
                    ''
                } else {
                    " This study should be compared against the previous study on ${patientRep.studies[Integer.parseInt(studyRep.compareTo)].studyDate}. Comparisons should reference the previous study by date, not UID. " +
                        "The report for the previous study is represented by the following JSON: ${objectMapper.writeValueAsString(patientOutput.generatedReports[Integer.parseInt(studyRep.compareTo)])}"
                }
            }()

            final String mainPrompt = BASE_RAD_PROMPT_SINGULAR +
                " The patient is ${patient.sex.name().toLowerCase()} and was born on ${TimeUtils.UNAMBIGUOUS_DATE.format(patient.dateOfBirth)}." +
                " The study you are currently evaluating is a ${studyRep.description}. ${comparison}" +
                promptReport.getUserMessage(study, studyRep) +
                fullComparisonSerialization

            final GeneratedReport generatedReport = new GenericLlmCall<>(
                client,
                BASE_RAD_CONTEXT_SINGULAR,
                mainPrompt,
                model,
                reportClass
            ).withValidation({ GeneratedReport report ->
                promptReport.preserveState(report)
                report.validateReport()
            }).issueQuery()
            generatedReport.setUid(studyRep.uid)
            patientOutput.generatedReports << generatedReport
        }

        patientOutput.generatedReports.each { generatedReport ->
            generatedReport.setUid(uidMapping.get(Integer.parseInt(generatedReport.getUid())))
        }
        patientOutput
    }

    List<PatientOutput> generateReportsForPatients(List<Patient> patients) {
        final List<String> uidMapping = []

        final ReportInput reportInput = new ReportInput(patients: patients.collect { patient ->
            convertToPatientRep(patient, uidMapping)
        })

        final List<PatientOutputClassic> patientOutputs = new ArrayWrapperLlmCall<>(
            client,
            BASE_RAD_CONTEXT,
            "${BASE_RAD_PROMPT} ${objectMapper.writeValueAsString(reportInput)}",
            model,
            PatientOutputArrayWrapper
        ).issueQueryUnwrapped()
        patientOutputs.each { patientOutput ->
            patientOutput.generatedReports.removeAll { ClassicReport generatedReport ->
                try {
                    generatedReport.setUid(uidMapping.get(Integer.parseInt(generatedReport.getUid())))
                    false
                } catch (Exception e) {
                    logger.warn('Error in converting data from LLM', e)
                    true
                }
            }
        }
        patientOutputs.collect {
            new PatientOutput(patientId: it.patientId, generatedReports: it.generatedReports)
        }
    }

    private static PatientRep convertToPatientRep(Patient patient, List<String> uidMapping) {
        final PatientRep patientRep = new PatientRep()
        patientRep.setSex(patient.sex.name().toLowerCase())
        patientRep.setPatientId(patient.patientIds[0].idNumber)
        patientRep.setDateOfBirth(TimeUtils.UNAMBIGUOUS_DATE.format(patient.dateOfBirth))

        final List<Study> sortedStudies = patient.studies.sort(false, { it.studyDateTime() })
        final Map<String, String> comparisons = [:]
        final Map<String, List<String>> studiesByStandardizedDescription = [:]

        sortedStudies.each { study ->
            uidMapping << study.studyInstanceUid
            final String description = study.simpleDescription
            final List<String> previousStudies = studiesByStandardizedDescription.computeIfAbsent(
                description,
                { [] }
            )
            if (!previousStudies.isEmpty()) {
                comparisons.put(study.studyInstanceUid, previousStudies.last())
            }
            previousStudies << study.studyInstanceUid
        }

        patientRep.setStudies(sortedStudies.collect { sortedStudy ->
            final StudyRep studyRep = new StudyRep()
            final String studyInstanceUid = sortedStudy.studyInstanceUid
            studyRep.setUid(String.valueOf(uidMapping.indexOf(studyInstanceUid)))
            studyRep.setDescription(sortedStudy.simpleDescription)
            studyRep.setStudyDate(TimeUtils.UNAMBIGUOUS_DATE.format(sortedStudy.studyDate))
            if (comparisons.containsKey(studyInstanceUid)) {
                studyRep.setCompareTo(String.valueOf(uidMapping.indexOf(comparisons.get(studyInstanceUid))))
            }
            studyRep
        })

        patientRep
    }

}
