package edu.washu.tag.generator.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.core.RequestOptions
import com.openai.errors.OpenAIIoException
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.StructuredChatCompletion
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.TimeUtils
import edu.washu.tag.util.FileIOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.function.Function

class OpenAiWrapper {

    private final OpenAIClient client
    private final String model
    private static final Logger logger = LoggerFactory.getLogger(OpenAiWrapper)
    private static final String BASE_RAD_CONTEXT = FileIOUtils.readResource('rad_context.txt')
    private static final String BASE_RAD_CONTEXT_SINGULAR = FileIOUtils.readResource('rad_context_singular.txt')
    private static final String BASE_RAD_PROMPT = FileIOUtils.readResource('rad_prompt.txt')
    private static final String BASE_RAD_PROMPT_SINGULAR = FileIOUtils.readResource('rad_prompt_singular.txt')
    private static final ObjectMapper objectMapper = new ObjectMapper()
    private static final int MAX_RETRIES = 5

    OpenAiWrapper(String endpoint, String apiKeyEnvVar, String modelName) {
        model = modelName
        client = OpenAIOkHttpClient.builder()
            .apiKey(System.getenv(apiKeyEnvVar))
            .baseUrl(endpoint)
            .build()
    }

    List<GeneratedReport> generateReportsForPatient(Patient patient) {
        final List<String> uidMapping = []
        final PatientRep patientRep = convertToPatientRep(patient, uidMapping)

        final String mainPrompt = BASE_RAD_PROMPT_SINGULAR
            + " The patient is ${patient.sex.name().toLowerCase()} and was born on ${TimeUtils.UNAMBIGUOUS_DATE.format(patient.dateOfBirth)}."
            + ' The studies to evaluate are: '
            + patientRep.studies.collect { study ->
                final String studyInstanceUid = study.uid
                final String comparison = study.compareTo != null ? " which should be compared against study ${study.compareTo}" : ''
                "a ${study.description} on ${study.studyDate} with a UID of ${studyInstanceUid}${comparison}"
            }.join(', ')
        logger.info("Main prompt: ${mainPrompt}")

        final List<GeneratedReport> generatedReports = new LlmCall<>(
            BASE_RAD_CONTEXT_SINGULAR,
            mainPrompt,
            model,
            GeneratedReportArrayWrapper
        ).withValidation { response ->
            patient.studies*.studyInstanceUid.every { uid ->
                uid in response*.uid
            }
        }.singletonQuery()
        generatedReports.each { generatedReport ->
            generatedReport.setUid(uidMapping.get(Integer.parseInt(generatedReport.getUid())))
        }
        generatedReports
    }

    List<PatientOutput> generateReportsForPatients(List<Patient> patients) {
        final List<String> uidMapping = []

        final ReportInput reportInput = new ReportInput(patients: patients.collect { patient ->
            convertToPatientRep(patient, uidMapping)
        })

        final List<PatientOutput> patientOutputs = new LlmCall<>(
            BASE_RAD_CONTEXT,
            BASE_RAD_PROMPT,
            model,
            reportInput,
            PatientOutputArrayWrapper
        ).singletonQuery()
        patientOutputs.each { patientOutput ->
            patientOutput.generatedReports.removeAll { generatedReport ->
                try {
                    generatedReport.setUid(uidMapping.get(Integer.parseInt(generatedReport.getUid())))
                    false
                } catch (Exception e) {
                    logger.warn('Error in converting data from LLM', e)
                    true
                }
            }
        }
        patientOutputs
    }

    private class LlmCall<X, U extends ModelArrayWrapper<X>> {
        private final StructuredChatCompletionCreateParams<U> prompt
        private final String model
        private final Class<U> wrapperClass
        private Function<List<X>, Boolean> responseValidator

        LlmCall(StructuredChatCompletionCreateParams<U> prompt, String model, Class<U> wrapperClass) {
            this.prompt = prompt
            this.model = model
            this.wrapperClass = wrapperClass
        }

        LlmCall(String systemMessage, String userMessage, String model, Class<U> wrapperClass) {
            this(
                ChatCompletionCreateParams.builder()
                    .addSystemMessage(systemMessage)
                    .addUserMessage(userMessage)
                    .responseFormat(wrapperClass)
                    .model(model)
                    .build(),
                model,
                wrapperClass
            )
        }

        LlmCall(String systemMessage, String userMessage, String model, Object input, Class<U> wrapperClass) {
            this(
                ChatCompletionCreateParams.builder()
                    .addSystemMessage(systemMessage)
                    .addUserMessage(userMessage)
                    .addUserMessage(objectMapper.writeValueAsString(input))
                    .responseFormat(wrapperClass)
                    .model(model)
                    .build(),
                model,
                wrapperClass
            )
        }

        LlmCall<X, U> withValidation(Function<List<X>, Boolean> validator) {
            responseValidator = validator
            this
        }

        List<X> singletonQuery() {
            singletonQueryFromPrompt(prompt)
        }

        List<X> singletonQueryFromPrompt(StructuredChatCompletionCreateParams<U> resolvedPrompt) {
            int retry = 0
            while (retry < MAX_RETRIES) {
                final RequestOptions requestOptions = RequestOptions.builder().timeout(Duration.ofMinutes(5)).build()
                try {
                    final StructuredChatCompletion<U> completion = client.chat().completions().create(resolvedPrompt, requestOptions)
                    final List<X> output = completion.choices().stream()
                        .flatMap(choice -> choice.message().content().stream())
                        .flatMap(array -> array.objects.stream())
                        .toList()

                    if (responseValidator == null) {
                        return output
                    } else {
                        // validate?
                        if (responseValidator.apply(output)) {
                            return output
                        } else {
                            logger.warn('LLM response failed custom validation')
                        }
                    }
                } catch (OpenAIIoException ioException) {
                    logger.warn('LLM query failed', ioException)
                }
                retry++
            }
            throw new RuntimeException('Failed to successfully call LLM')
        }
    }

    private static convertToPatientRep(Patient patient, List<String> uidMapping) {
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
