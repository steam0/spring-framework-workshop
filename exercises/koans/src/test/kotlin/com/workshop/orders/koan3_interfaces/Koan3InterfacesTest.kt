package com.workshop.orders.koan3_interfaces

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Koan 3: Why Interfaces Matter
 *
 * 1. Make StripeGateway implement PaymentGateway and annotate it as a Spring component.
 * 2. Change OrderService to depend on PaymentGateway (the interface), not StripeGateway.
 *
 * Hint: The constructor parameter type should be the interface.
 */
@Configuration
@ComponentScan(basePackages = ["com.workshop.orders.koan3_interfaces"])
class Koan3TestConfig

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [Koan3TestConfig::class])
class Koan3InterfacesTest {

    @Autowired
    lateinit var gateway: PaymentGateway

    @Autowired
    lateinit var orderService: OrderService

    @Test
    fun `PaymentGateway bean should exist and be a StripeGateway`() {
        assertThat(gateway).isInstanceOf(StripeGateway::class.java)
    }

    @Test
    fun `OrderService should place an order using the gateway`() {
        assertThat(orderService.placeOrder(500)).isTrue()
    }

    @Test
    fun `OrderService should depend on the interface, not the implementation`() {
        val constructorParamType = OrderService::class.constructors.first().parameters.first().type
        assertThat(constructorParamType.classifier).isEqualTo(PaymentGateway::class)
    }
}
