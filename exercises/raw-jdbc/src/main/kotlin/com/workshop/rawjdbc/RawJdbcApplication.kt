package com.workshop.rawjdbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RawJdbcApplication

fun main(args: Array<String>) {
    runApplication<RawJdbcApplication>(*args)
}
