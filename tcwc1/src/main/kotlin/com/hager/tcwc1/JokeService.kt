package com.hager.tcwc1

import org.springframework.stereotype.Service


@Service
class JokeService(
    private val geminiClient: GeminiClient
) {

    fun generateJoke(): String {
        return geminiClient.getJoke()
    }
}