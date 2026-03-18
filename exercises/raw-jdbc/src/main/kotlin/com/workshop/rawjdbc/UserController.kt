package com.workshop.rawjdbc

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userRepository: UserRepository) {

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): User? = userRepository.fetchUser(id)
}
