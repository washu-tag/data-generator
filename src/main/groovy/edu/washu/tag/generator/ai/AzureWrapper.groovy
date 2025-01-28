package edu.washu.tag.generator.ai

import com.azure.ai.openai.OpenAIClient
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.*
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder
import com.azure.core.util.BinaryData
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.util.FileIOUtils
import edu.washu.tag.generator.util.TimeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.function.Consumer
import java.util.function.Function

class AzureWrapper {

    private final OpenAIClient client
    private static final Logger logger = LoggerFactory.getLogger(AzureWrapper)
    private static final String MODEL = 'gpt-4o-mini'
    private static final String API_KEY = System.getenv('AZURE_OPENAI_KEY')
    private static final String AZURE_RESOURCE_NAME = System.getenv('AZURE_RESOURCE_NAME')

    private static final String BASE_RAD_CONTEXT = FileIOUtils.readResource('rad_context.txt')
    private static final String BASE_RAD_PROMPT = FileIOUtils.readResource('rad_prompt.txt')

    private static final ChatCompletionsResponseFormat REPORTS_RESPONSE_FORMAT = new ChatCompletionsJsonSchemaResponseFormat(
            new ChatCompletionsJsonSchemaResponseFormatJsonSchema('get_report')
                    .setStrict(true)
                    .setSchema(BinaryData.fromString(FileIOUtils.readResource('azure_report_longitudinal_schema.json')))
    )

    private static final int MAX_RETRIES = 5
    private static final int MAX_OBJECTS_PER_ARRAY = 50

    AzureWrapper() {
        client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(API_KEY))
                .endpoint("https://${AZURE_RESOURCE_NAME}.openai.azure.com")
                .httpClient(
                        new NettyAsyncHttpClientBuilder()
                                .writeTimeout(Duration.ofMinutes(2))
                                .build()
                ).buildClient()
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

        final List<ChatRequestMessage> prompt = [
                new ChatRequestSystemMessage(BASE_RAD_CONTEXT + "The patient is ${patient.sex.name().toLowerCase()} and was born on ${TimeUtils.UNAMBIGUOUS_DATE.format(patient.dateOfBirth)}."),
                new ChatRequestUserMessage(mainPrompt)
        ]

        new AzureCall<>(prompt, REPORTS_RESPONSE_FORMAT, GeneratedReportArrayWrapper)
                .withValidation { response ->
                    patient.studies*.studyInstanceUid.every { uid ->
                        uid in response*.uid
                    }
                }.singletonQuery()
    }

    static boolean attempt() {
        API_KEY != null && AZURE_RESOURCE_NAME != null
    }

    private class AzureCall<X, U extends ModelArrayWrapper<X>> {
        private final List<ChatRequestMessage> prompt
        private final ChatCompletionsResponseFormat responseFormat
        private final Class<U> wrapperClass
        private final int maxItemsInArray
        private Function<List<X>, Boolean> responseValidator

        AzureCall(List<ChatRequestMessage> prompt, ChatCompletionsResponseFormat responseFormat, Class<U> wrapperClass, int maxItemsInArray = MAX_OBJECTS_PER_ARRAY) {
            this.prompt = prompt
            this.responseFormat = responseFormat
            this.wrapperClass = wrapperClass
            this.maxItemsInArray = maxItemsInArray
        }

        AzureCall<X, U> withValidation(Function<List<X>, Boolean> validator) {
            responseValidator = validator
            this
        }

        List<X> queryLlm(int numRequested) {
            recursivelyQueryLlm(numRequested, [])
        }

        List<X> singletonQuery() {
            singletonQueryFromPrompt(prompt)
        }

        List<X> singletonQueryFromPrompt(List<ChatRequestMessage> resolvedPrompt) {
            final ChatCompletionsOptions chatCompletionsOptions =
                    new ChatCompletionsOptions(resolvedPrompt).setResponseFormat(responseFormat)

            int retry = 0
            while(retry < MAX_RETRIES) {
                for (ChatChoice chatChoice : client.getChatCompletions(MODEL, chatCompletionsOptions).getChoices()) {
                    if (chatChoice.getFinishReason() == CompletionsFinishReason.STOPPED) {
                        final String asString = chatChoice.message.content
                        if (asString == null) {
                            logger.info('LLM returned null String, skipping...')
                        } else {
                            logger.info('Deserializing: {}', asString)
                            try {
                                final List<X> objects = BinaryData.fromString(asString).toObject(wrapperClass).objects
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
                            } catch (UncheckedIOException ignored) {
                                logger.info('Failed to parse previous String.')
                            }
                        }
                    } else {
                        logger.warn('LLM query failed with finish reason {}', chatChoice.finishReason)
                    }
                }
                retry++
            }
            throw new RuntimeException('Failed to successfully call LLM')
        }

        private List<X> recursivelyQueryLlm(int numRequested, List<X> generatedObjects) {
            final Consumer<Integer> performQueryFor = (numToRequest) -> {
                final List<ChatRequestMessage> promptWithSize = new ArrayList<>(prompt)
                promptWithSize.addFirst(new ChatRequestSystemMessage(
                        "You will be generating some structured data, which contains an array called objects. That array should contain exactly ${numRequested} elements."
                ))

                generatedObjects.addAll(singletonQueryFromPrompt(promptWithSize))
            }

            final int remainingObjectsNeeded = numRequested - generatedObjects.size()
            final int numMaxRequestsToMake = remainingObjectsNeeded / maxItemsInArray
            final int numRemainingRequests = remainingObjectsNeeded % maxItemsInArray

            numMaxRequestsToMake.times {
                performQueryFor.accept(maxItemsInArray)
            }

            if (numRemainingRequests > 0) {
                performQueryFor.accept(numRemainingRequests)
            }

            if (generatedObjects.size() >= numRequested) {
                return generatedObjects
            }

            logger.info(
                    'Going back to LLM to attempt to generate additional data. Current list size: {}. Requested list size: {}.',
                    generatedObjects.size(), numRequested)
            return recursivelyQueryLlm(numRequested, generatedObjects)
        }

    }

}
