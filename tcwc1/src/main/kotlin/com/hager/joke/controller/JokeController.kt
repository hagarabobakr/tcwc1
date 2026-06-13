package com.hager.joke.controller

import com.hager.joke.model.JokeResponse
import com.hager.joke.service.GroqJokeProvider
import com.hager.joke.service.JokeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class JokeController(
    private val jokeService: JokeService,
) {
    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf("status" to "ok")

    @GetMapping("/hager/joke")
    fun joke(@RequestParam word: String): ResponseEntity<JokeResponse> {
        val trimmedWord = word.trim()
        require(trimmedWord.isNotBlank()) { "Query parameter 'word' is required." }

        return ResponseEntity.ok(JokeResponse(joke = jokeService.generateJoke(trimmedWord)))
    }
}
