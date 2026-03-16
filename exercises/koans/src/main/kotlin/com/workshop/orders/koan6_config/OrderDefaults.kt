package com.workshop.orders.koan6_config

import org.springframework.stereotype.Service

@Service
class OrderDefaults(
    // TODO: Inject OrderProperties here
) {
    fun maxItems(): Int = TODO("Return maxItems from properties")
    fun currency(): String = TODO("Return currency from properties")
    fun discountPercent(): Int = TODO("Return discountPercent from properties")
}
