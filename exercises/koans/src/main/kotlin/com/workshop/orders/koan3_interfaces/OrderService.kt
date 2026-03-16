package com.workshop.orders.koan3_interfaces

import org.springframework.stereotype.Service

@Service("koan3OrderService")
class OrderService(
    // TODO: Change the type from StripeGateway to PaymentGateway
    private val gateway: StripeGateway
) {
    fun placeOrder(amount: Long): Boolean = gateway.charge(amount)
}
