package com.workshop.orders.koan4_beans

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDate

/**
 * Koan 4: Declaring Beans for Third-Party Classes
 *
 * You can't add @Component to classes you don't own (like JsonMapper).
 * Instead, declare a @Bean method inside a @Configuration class.
 *
 * 1. Annotate AppConfig with @Configuration
 * 2. Add a @Bean method that returns a JsonMapper
 *
 * Hint:
 *   JsonMapper.builder().build()
 */
@Configuration
@ComponentScan(basePackages = ["com.workshop.orders.koan4_beans"])
class Koan4TestConfig

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [Koan4TestConfig::class])
class Koan4BeansTest {

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Autowired
    lateinit var serializer: OrderSerializer

    @Test
    fun `JsonMapper bean should exist`() {
        assertThat(jsonMapper).isNotNull()
    }

    @Test
    fun `JsonMapper should serialize dates as ISO-8601 strings`() {
        val data = mapOf("date" to LocalDate.of(2026, 3, 16))
        val json = serializer.serialize(data)
        // Jackson 3 serializes LocalDate as "2026-03-16" by default
        assertThat(json).contains("2026-03-16")
    }
}
