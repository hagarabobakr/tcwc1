package com.hager.tcwc1

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GeminiClient {

    private val restTemplate = RestTemplate()

    fun getJoke(): String {


        return "Why do programmers love coffee? Because it helps them debug."
    }
}