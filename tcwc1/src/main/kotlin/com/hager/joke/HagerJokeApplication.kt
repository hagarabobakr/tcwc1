package com.hager.joke

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HagerJokeApplication

fun main(args: Array<String>) {
	runApplication<HagerJokeApplication>(*args)
}
