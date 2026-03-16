package com.workshop.orders.koan1_ioc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Koan 1: Inversion of Control
 *
 * Make PriceCalculator a Spring-managed bean so the container can create it for you.
 *
 * Hint: Look at the @Component annotation.
 */
@Configuration
@ComponentScan(basePackages = ["com.workshop.orders.koan1_ioc"])
class Koan1TestConfig

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [Koan1TestConfig::class])
class Koan1IoCTest {

    @Autowired
    lateinit var calculator: PriceCalculator

    @Test
    fun `Spring should create a PriceCalculator bean`() {
        assertThat(calculator).isNotNull()
    }

    @Test
    fun `PriceCalculator should calculate total price`() {
        assertThat(calculator.calculate(100, 3)).isEqualTo(300)
    }
}
