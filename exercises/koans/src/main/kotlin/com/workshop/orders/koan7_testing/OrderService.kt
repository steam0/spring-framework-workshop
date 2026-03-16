package com.workshop.orders.koan7_testing

import org.springframework.stereotype.Service

@Service("koan7OrderService")
class OrderService(private val repository: OrderRepository) {

    fun findByStatus(status: String): List<Order> = repository.findByStatus(status)

    fun create(item: String, quantity: Int): Order =
        repository.save(Order(item = item, quantity = quantity))
}
