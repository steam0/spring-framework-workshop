package com.workshop.orders.koan1_ioc

import org.springframework.stereotype.Component

// TODO: Make this class a Spring-managed component
class PriceCalculator {
    fun calculate(itemPrice: Long, quantity: Int): Long = itemPrice * quantity
}
