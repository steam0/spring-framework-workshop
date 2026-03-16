package com.workshop.orders.koan5_autowiring

import org.springframework.stereotype.Component

@Component("vipps")
class VippsGateway : PaymentGateway {
    override fun name(): String = "vipps"
    override fun charge(amount: Long): Boolean = amount > 0
}
