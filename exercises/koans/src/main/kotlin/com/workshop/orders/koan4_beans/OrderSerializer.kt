package com.workshop.orders.koan4_beans

import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class OrderSerializer(private val jsonMapper: JsonMapper) {
    fun serialize(data: Map<String, Any>): String = jsonMapper.writeValueAsString(data)
}
