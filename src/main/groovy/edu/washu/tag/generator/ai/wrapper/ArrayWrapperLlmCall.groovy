package edu.washu.tag.generator.ai.wrapper

import com.openai.client.OpenAIClient
import com.openai.models.responses.StructuredResponseCreateParams
import edu.washu.tag.generator.ai.ModelArrayWrapper

import java.util.function.Function

class ArrayWrapperLlmCall<T, A extends ModelArrayWrapper<T>> extends GenericLlmCall<A, ArrayWrapperLlmCall<T, A>> {

    ArrayWrapperLlmCall(OpenAIClient client, StructuredResponseCreateParams<A> prompt, String model) {
        super(client, prompt, model)
    }

    ArrayWrapperLlmCall(OpenAIClient client, String systemMessage, String userMessage, String model, Class<A> responseClass) {
        super(client, systemMessage, userMessage, model, responseClass)
    }

    ArrayWrapperLlmCall<T, A> validating(Function<List<T>, Boolean> validator) {
        withValidation({ validator.apply(it.objects) })
    }

    List<T> issueQueryUnwrapped() {
        issueQuery().objects
    }

}
