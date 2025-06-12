package edu.washu.tag.generator.ai

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.StructuredChatCompletion
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.TimeUtils
import edu.washu.tag.util.FileIOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Function

class OpenAiWrapper {

    private final OpenAIClient client
    private final String model
    private static final Logger logger = LoggerFactory.getLogger(OpenAiWrapper)
    private static final String BASE_RAD_CONTEXT = FileIOUtils.readResource('rad_context.txt')
    private static final String BASE_RAD_PROMPT = FileIOUtils.readResource('rad_prompt.txt')

    private static final int MAX_RETRIES = 5

    OpenAiWrapper(String endpoint, String apiKeyEnvVar, String modelName) {
        model = modelName
        client = OpenAIOkHttpClient.builder()
            .apiKey(System.getenv(apiKeyEnvVar))
            .baseUrl(endpoint)
            .build()
    }

    List<GeneratedReport> generateReports(Patient patient) {
        final Closure<String> studyPrompter = {
            final List<Study> sortedStudies = patient.studies.sort(false, { it.studyDateTime() })
            final Map<String, String> comparisons = [:]

            final Map<String, List<String>> studiesByStandardizedDescription = [:]
            sortedStudies.each { study ->
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

            sortedStudies.collect { study ->
                final String studyInstanceUid = study.studyInstanceUid
                final String comparison = comparisons.containsKey(studyInstanceUid) ? " which should be compared against study ${comparisons[studyInstanceUid]}" : ''
                "a ${study.simpleDescription} on ${TimeUtils.UNAMBIGUOUS_DATE.format(study.studyDate)} with a UID of ${studyInstanceUid}${comparison}"
            }.join(',')
        }

        final String mainPrompt = BASE_RAD_PROMPT + studyPrompter()
        logger.info("Main prompt: ${mainPrompt}")

        new LlmCall<GeneratedReport>(
            "${BASE_RAD_CONTEXT} The patient is ${patient.sex.name().toLowerCase()} and was born on ${TimeUtils.UNAMBIGUOUS_DATE.format(patient.dateOfBirth)}.",
            mainPrompt
        ).withValidation { response ->
            patient.studies*.studyInstanceUid.every { uid ->
                uid in response*.uid
            }
        }.singletonQuery()
    }

    private class LlmCall<X> {
        private final StructuredChatCompletionCreateParams<ModelArrayWrapper<X>> prompt
        private Function<List<X>, Boolean> responseValidator

        LlmCall(StructuredChatCompletionCreateParams<ModelArrayWrapper<X>> prompt) {
            this.prompt = prompt
        }

        LlmCall(String systemMessage, String userMessage) {
            this(
                ChatCompletionCreateParams.builder()
                    .model(model)
                    .responseFormat(ModelArrayWrapper<X>)
                    .addSystemMessage(systemMessage)
                    .addUserMessage(userMessage)
                    .build()
            )
        }


        LlmCall<X> withValidation(Function<List<X>, Boolean> validator) {
            responseValidator = validator
            this
        }

        List<X> singletonQuery() {
            singletonQueryFromPrompt(prompt)
        }

        List<X> singletonQueryFromPrompt(StructuredChatCompletionCreateParams<ModelArrayWrapper<X>> resolvedPrompt) {
            int retry = 0
            while (retry < MAX_RETRIES) {
                for (StructuredChatCompletion.Choice<ModelArrayWrapper<X>> chatChoice : client.chat().completions().create(resolvedPrompt).choices()) {
                    if (chatChoice.finishReason().asString() == 'stop') {
                        final List<X> objects = chatChoice.message().content().get().objects
                        if (responseValidator == null) {
                            return objects
                        } else {
                            // validate?
                            if (responseValidator.apply(objects)) {
                                return objects
                            } else {
                                logger.warn('LLM response failed custom validation')
                            }
                        }
                    } else {
                        logger.warn('LLM query failed with finish reason {}', chatChoice.finishReason().asString())
                    }
                }
                retry++
            }
            throw new RuntimeException('Failed to successfully call LLM')
        }
    }

}
