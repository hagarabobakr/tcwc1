package com.hager.joke.service

import org.springframework.stereotype.Service

@Service
class JokeService(
    private val jokeProvider: JokeProvider,
) {
    fun generateJoke(word: String): String = jokeProvider.generateJoke(word)
}
