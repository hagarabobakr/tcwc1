package com.hager.tcwc1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hager")
class JokeController(
    private val jokeService: JokeService
) {

    @GetMapping
    fun getJoke(
        @RequestParam word: String
    ): JokeResponse {

        return JokeResponse(
            jokeService.generateJoke(word)
        )
    }
}