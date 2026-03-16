package com.workshop.orders.koan5_autowiring

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Koan 5: Autowiring with @Qualifier
 *
 * There are two PaymentGateway beans: "stripe" and "vipps".
 * Spring doesn't know which one to inject — you need to tell it.
 *
 * Add @Qualifier("vipps") to the gateway parameter in CheckoutService.
 */
@Configuration
@ComponentScan(basePackages = ["com.workshop.orders.koan5_autowiring"])
class Koan5TestConfig

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [Koan5TestConfig::class])
class Koan5AutowiringTest {

    @Autowired
    lateinit var checkoutService: CheckoutService

    @Autowired
    lateinit var beanFactory: DefaultListableBeanFactory

    @Test
    fun `CheckoutService should use the vipps gateway`() {
        val result = checkoutService.checkout(100)
        assertThat(result).startsWith("vipps")
    }

    @Test
    fun `there should be two PaymentGateway beans in the context`() {
        val count = beanFactory.getBeanNamesForType(PaymentGateway::class.java).size
        assertThat(count).isEqualTo(2)
    }
}
