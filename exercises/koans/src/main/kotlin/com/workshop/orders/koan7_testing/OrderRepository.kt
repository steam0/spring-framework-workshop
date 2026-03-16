package com.workshop.orders.koan7_testing

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByStatus(status: String): List<Order>
}
