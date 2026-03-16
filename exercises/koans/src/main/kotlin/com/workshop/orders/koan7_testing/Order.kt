package com.workshop.orders.koan7_testing

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(
    var item: String = "",
    var quantity: Int = 0,
    var status: String = "CREATED",
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
