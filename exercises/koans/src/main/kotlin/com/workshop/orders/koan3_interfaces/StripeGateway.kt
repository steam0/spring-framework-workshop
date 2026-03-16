package com.workshop.orders.koan3_interfaces

import org.springframework.stereotype.Component

// This class already implements PaymentGateway and is a Spring component.
// But look at OrderService — it depends on StripeGateway directly.
// TODO: Change OrderService to depend on the PaymentGateway interface instead.
@Component("koan3StripeGateway")
class StripeGateway : PaymentGateway {
    override fun charge(amount: Long): Boolean = amount > 0
}
