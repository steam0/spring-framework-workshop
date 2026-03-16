package com.workshop.orders.koan6_config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Koan 6: Configuration and Profiles
 *
 * 1. Annotate OrderProperties with @ConfigurationProperties(prefix = "orders") and @Component
 * 2. Inject OrderProperties into OrderDefaults via constructor
 * 3. Implement the three methods by delegating to the properties
 *
 * The second test activates the "premium" profile which overrides discount-percent to 15.
 */
@SpringBootApplication(scanBasePackages = ["com.workshop.orders.koan6_config"])
class Koan6TestApp

@SpringBootTest(classes = [Koan6TestApp::class])
class Koan6ConfigDefaultTest {

    @Autowired
    lateinit var defaults: OrderDefaults

    @Test
    fun `should read max-items from application yml`() {
        assertThat(defaults.maxItems()).isEqualTo(10)
    }

    @Test
    fun `should read currency from application yml`() {
        assertThat(defaults.currency()).isEqualTo("NOK")
    }

    @Test
    fun `should read default discount-percent as 0`() {
        assertThat(defaults.discountPercent()).isEqualTo(0)
    }
}

@SpringBootTest(classes = [Koan6TestApp::class])
@ActiveProfiles("premium")
class Koan6ConfigPremiumTest {

    @Autowired
    lateinit var defaults: OrderDefaults

    @Test
    fun `premium profile should override discount-percent to 15`() {
        assertThat(defaults.discountPercent()).isEqualTo(15)
    }

    @Test
    fun `premium profile should still use default max-items`() {
        assertThat(defaults.maxItems()).isEqualTo(10)
    }
}
