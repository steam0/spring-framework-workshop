package com.workshop.orders.koan5_autowiring

interface PaymentGateway {
    fun name(): String
    fun charge(amount: Long): Boolean
}
