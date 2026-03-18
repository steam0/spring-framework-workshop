package com.workshop.rawjdbc

import org.springframework.stereotype.Component

@Component
class InMemoryUserRepository : UserRepository {

    private val users = mapOf(
        1L to User(1, "Ola Nordmann"),
        2L to User(2, "Kari Nordmann"),
        3L to User(3, "Per Hansen"),
    )

    override fun fetchUser(userId: Long): User? = users[userId]
}
