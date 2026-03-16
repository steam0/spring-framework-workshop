package com.workshop.orders.koan3_interfaces

interface PaymentGateway {
    fun charge(amount: Long): Boolean
}
