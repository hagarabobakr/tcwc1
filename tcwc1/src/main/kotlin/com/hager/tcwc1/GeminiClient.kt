package com.hager.tcwc1

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

@Component
class GeminiClient(

    private val webClient: WebClient,

    @Value("\${gemini.api.key}")
    private val apiKey: String,

    @Value("\${gemini.api.url}")
    private val apiUrl: String
) {

    private val restTemplate = RestTemplate()

    fun getJoke(word: String): String {

        val prompt = """
        Generate a short funny joke in Egyptian Arabic.

        The joke should be based on this word: $word

        Requirements:
        - Egyptian dialect.
        - Family friendly.
        - Different response every time.
        - Return only the joke text.
    """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(prompt)
                    )
                )
            )
        )

        val response = webClient.post()
            .uri("$apiUrl?key=$apiKey")
            .bodyValue(request)
            .exchangeToMono { response ->
                if (response.statusCode().isError) {
                    response.bodyToMono(String::class.java)
                        .map { body ->
                            throw RuntimeException("Gemini Error: $body")
                        }
                } else {
                    response.bodyToMono(GeminiResponse::class.java)
                }
            }.block()

        return response
            ?.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: "معرفتش أطلع نكتة دلوقتي 😅"
    }
}