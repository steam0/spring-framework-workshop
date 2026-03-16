package com.workshop.orders.koan2_di

import org.springframework.stereotype.Service

@Service("koan2OrderService")
class OrderService {
    // TODO: Inject PriceCalculator via constructor injection

    
    fun totalFor(itemPrice: Long, quantity: Int): Long = TODO("Use the calculator")
}
