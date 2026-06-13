package com.hager.joke.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
@ConditionalOnProperty(name = ["llm.provider"], havingValue = "groq")
class GroqJokeProvider(
    @Value("\${groq.api-key}") rawApiKey: String,
    @Value("\${groq.model:llama-3.3-70b-versatile}") private val model: String,
) : JokeProvider {
    private val apiKey = rawApiKey.trim().trim('"', '\'')
    private val restTemplate = RestTemplate()

    override fun generateJoke(word: String): String {
        require(apiKey.isNotBlank()) {
            "Groq API key is not configured. Set GROQ_API_KEY and restart the app."
        }

        val request = GroqChatRequest(
            model = model,
            messages = listOf(GroqMessage(role = "user", content = JokePromptBuilder.build(word))),
            temperature = 1.0
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(apiKey)
        }

        return try {
            val response = restTemplate.postForObject(
                "https://api.groq.com/openai/v1/chat/completions",
                HttpEntity(request, headers),
                GroqChatResponse::class.java,
            ) ?: throw IllegalStateException("Empty response from Groq.")

            response.choices
                ?.firstOrNull()
                ?.message
                ?.content
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Groq returned no joke text.")
        } catch (ex: HttpClientErrorException.Unauthorized) {
            throw IllegalStateException(
                "Groq rejected the API key (401). Create a free key at https://console.groq.com/keys.",
                ex,
            )
        } catch (ex: RestClientException) {
            throw IllegalStateException("Failed to generate joke with Groq: ${ex.message}", ex)
        }
    }
}

data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double
)

data class GroqMessage(val role: String, val content: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GroqChatResponse(val choices: List<GroqChoice>? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GroqChoice(val message: GroqMessage? = null)
