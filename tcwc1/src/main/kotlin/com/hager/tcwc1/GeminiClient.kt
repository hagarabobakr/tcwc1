package com.hager.tcwc1

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GeminiClient {

    private val restTemplate = RestTemplate()

    fun getJoke(): String {


        return "مرة عمارتين قابلو موزة قالولها مين الي بنانا"
    }
}