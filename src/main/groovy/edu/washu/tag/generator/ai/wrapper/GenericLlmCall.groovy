package edu.washu.tag.generator.ai.wrapper

import com.openai.client.OpenAIClient
import com.openai.core.RequestOptions
import com.openai.errors.OpenAIInvalidDataException
import com.openai.errors.OpenAIIoException
import com.openai.models.responses.StructuredResponse
import com.openai.models.responses.StructuredResponseCreateParams
import com.openai.models.responses.StructuredResponseOutputMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.function.Function

class GenericLlmCall<T, S extends GenericLlmCall<T, S>> {

    private final OpenAIClient client
    private final StructuredResponseCreateParams<T> prompt
    private final String model
    private Function<T, Boolean> responseValidator
    private static final int MAX_RETRIES = 10
    private static final Logger logger = LoggerFactory.getLogger(GenericLlmCall)

    GenericLlmCall(OpenAIClient client, StructuredResponseCreateParams<T> prompt, String model) {
        this.client = client
        this.prompt = prompt
        this.model = model
    }

    GenericLlmCall(OpenAIClient client, String systemMessage, String userMessage, String model, Class<T> responseClass) {
        this(
            client,
            StructuredResponseCreateParams.builder()
                .instructions(systemMessage)
                .input(userMessage)
                .text(responseClass)
                .model(model)
                .build() as StructuredResponseCreateParams<T>,
            model
        )
    }

    S withValidation(Function<T, Boolean> validator) {
        responseValidator = validator
        this as S
    }

    T issueQuery() {
        int retry = 0
        while (retry < MAX_RETRIES) {
            final RequestOptions requestOptions = RequestOptions.builder().timeout(Duration.ofMinutes(5)).build()
            try {
                logger.info('Calling LLM...')
                final StructuredResponse<T> completion = client.responses().create(prompt, requestOptions)
                final List<StructuredResponseOutputMessage<T>> responseItems = completion.output().findResults {
                    it.message().isPresent() ? it.message().get() : null
                }
                if (responseItems.size() > 1) {
                    logger.warn('LLM response included multiple output items')
                }
                final T output = responseItems[0].content()[0].outputText().get()

                if (responseValidator == null) {
                    return output
                } else {
                    if (responseValidator.apply(output)) {
                        return output
                    } else {
                        logger.warn('LLM response failed custom validation')
                    }
                }
            } catch (OpenAIIoException | OpenAIInvalidDataException oaiException) {
                logger.warn('LLM query failed', oaiException)
            }
            retry++
        }
        throw new RuntimeException('Failed to successfully call LLM')
    }
}
