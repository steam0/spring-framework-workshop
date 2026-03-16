package com.workshop.orders.koan5_autowiring

import org.springframework.stereotype.Component

@Component("stripe")
class StripeGateway : PaymentGateway {
    override fun name(): String = "stripe"
    override fun charge(amount: Long): Boolean = amount > 0
}
