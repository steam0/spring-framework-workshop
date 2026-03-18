package com.workshop.rawjdbc

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class JdbcUserRepository(private val dataSource: DataSource) : UserRepository {

    @PostConstruct
    fun init() {
        dataSource.connection.use { conn ->
            conn.createStatement().execute(
                """
                CREATE TABLE IF NOT EXISTS users (
                    user_id BIGINT PRIMARY KEY,
                    name VARCHAR(255)
                )
                """.trimIndent()
            )
            conn.createStatement().execute("INSERT INTO users VALUES (1, 'Ola Nordmann')")
            conn.createStatement().execute("INSERT INTO users VALUES (2, 'Kari Nordmann')")
            conn.createStatement().execute("INSERT INTO users VALUES (3, 'Per Hansen')")
        }
    }

    override fun fetchUser(userId: Long): User? {
        dataSource.connection.use { conn ->
            val ps = conn.prepareStatement("SELECT user_id, name FROM users WHERE user_id = ?")
            ps.setLong(1, userId)
            val rs = ps.executeQuery()
            return if (rs.next()) User(rs.getLong("user_id"), rs.getString("name")) else null
        }
    }
}
