package com.workshop.orders.koan2_di

import org.springframework.stereotype.Component

@Component("koan2PriceCalculator")
class PriceCalculator {
    fun calculate(itemPrice: Long, quantity: Int): Long = itemPrice * quantity
}
