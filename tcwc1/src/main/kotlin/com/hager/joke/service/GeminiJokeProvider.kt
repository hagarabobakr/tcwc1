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
@ConditionalOnProperty(name = ["llm.provider"], havingValue = "gemini", matchIfMissing = true)
class GeminiJokeProvider(
    @Value("\${gemini.api-key}") rawApiKey: String,
    @Value("\${gemini.model:gemini-2.0-flash-lite}") private val model: String,
) : JokeProvider {
    private val apiKey = rawApiKey.trim().trim('"', '\'')
    private val restTemplate = RestTemplate()

    override fun generateJoke(word: String): String {
        require(apiKey.isNotBlank()) {
            "Gemini API key is not configured. Set GEMINI_API_KEY and restart the app."
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = JokePromptBuilder.build(word)))),
            ),
            generationConfig = GenerationConfig(temperature = 1.4, topP = 0.95),
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("x-goog-api-key", apiKey)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

        return try {
            val response = restTemplate.postForObject(
                url,
                HttpEntity(request, headers),
                GeminiResponse::class.java,
            ) ?: throw IllegalStateException("Empty response from Gemini.")

            response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Gemini returned no joke text.")
        } catch (ex: HttpClientErrorException.Unauthorized) {
            throw IllegalStateException(
                "Gemini rejected the API key (401). Create a new key at https://aistudio.google.com/apikey.",
                ex,
            )
        } catch (ex: HttpClientErrorException.TooManyRequests) {
            throw IllegalStateException(
                "Gemini free quota exceeded (429). Switch to Groq: set LLM_PROVIDER=groq and GROQ_API_KEY " +
                    "(free key at https://console.groq.com/keys), or try GEMINI_MODEL=gemini-2.0-flash-lite.",
                ex,
            )
        } catch (ex: RestClientException) {
            throw IllegalStateException("Failed to generate joke with Gemini: ${ex.message}", ex)
        }
    }
}

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig,
)

data class GeminiContent(val parts: List<GeminiPart>)
data class GeminiPart(val text: String)
data class GenerationConfig(val temperature: Double, val topP: Double)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponse(val candidates: List<GeminiCandidate>? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiCandidate(val content: GeminiContent? = null)
