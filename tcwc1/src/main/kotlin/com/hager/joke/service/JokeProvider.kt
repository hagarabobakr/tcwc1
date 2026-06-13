package com.hager.joke.service

interface JokeProvider {
    fun generateJoke(word: String): String
}
