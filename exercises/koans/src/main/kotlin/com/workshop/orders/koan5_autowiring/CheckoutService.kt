package com.workshop.orders.koan5_autowiring

import org.springframework.stereotype.Service

@Service
class CheckoutService(
    // TODO: Spring finds two PaymentGateway beans (stripe and vipps).
    //       Use @Qualifier to tell Spring you want the "vipps" one.
    private val gateway: PaymentGateway
) {
    fun checkout(amount: Long): String = "${gateway.name()}: ${gateway.charge(amount)}"
}
