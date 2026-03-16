package com.workshop.orders.koan7_testing

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("koan7OrderController")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/orders")
    fun getByStatus(@RequestParam status: String): List<Order> =
        orderService.findByStatus(status)
}
