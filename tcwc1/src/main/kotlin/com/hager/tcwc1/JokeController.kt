package com.hager.tcwc1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class JokeController(
    private val jokeService: JokeService
) {

    @GetMapping("/hager")
    fun getJoke(): JokeResponse {
        return JokeResponse(
            jokeService.generateJoke()
        )
    }
}