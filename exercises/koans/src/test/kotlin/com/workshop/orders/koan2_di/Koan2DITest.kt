package com.workshop.orders.koan2_di

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Koan 2: Dependency Injection
 *
 * Inject PriceCalculator into OrderService via constructor injection.
 * Then implement totalFor() by delegating to the calculator.
 *
 * Hint: Add a constructor parameter and use it in the method body.
 */
@Configuration
@ComponentScan(basePackages = ["com.workshop.orders.koan2_di"])
class Koan2TestConfig

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [Koan2TestConfig::class])
class Koan2DITest {

    @Autowired
    lateinit var orderService: OrderService

    @Test
    fun `OrderService should calculate total using injected PriceCalculator`() {
        assertThat(orderService.totalFor(250, 4)).isEqualTo(1000)
    }

    @Test
    fun `OrderService should handle single item`() {
        assertThat(orderService.totalFor(99, 1)).isEqualTo(99)
    }
}
