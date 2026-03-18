package com.workshop.rawjdbc

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Primary
@Component
class NamedJdbcUserRepository(
    private val jdbc: NamedParameterJdbcTemplate,
) : UserRepository {

    @PostConstruct
    fun init() {
        jdbc.jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS users (
                user_id BIGINT PRIMARY KEY,
                name VARCHAR(255)
            )
            """.trimIndent()
        )
        jdbc.update("INSERT INTO users VALUES (:id, :name)", mapOf("id" to 1L, "name" to "Ola Nordmann"))
        jdbc.update("INSERT INTO users VALUES (:id, :name)", mapOf("id" to 2L, "name" to "Kari Nordmann"))
        jdbc.update("INSERT INTO users VALUES (:id, :name)", mapOf("id" to 3L, "name" to "Per Hansen"))
    }

    override fun fetchUser(userId: Long): User? =
        jdbc.query(
            "SELECT user_id, name FROM users WHERE user_id = :userId",
            mapOf("userId" to userId),
        ) { rs, _ -> User(rs.getLong("user_id"), rs.getString("name")) }
            .firstOrNull()
}
