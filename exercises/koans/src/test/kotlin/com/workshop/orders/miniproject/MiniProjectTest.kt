package com.workshop.orders.miniproject

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Mini-Project: Build a complete Order API from scratch.
 *
 * The miniproject package is empty — you create everything:
 *   1. An @Entity class (e.g. MiniOrder) with: id, item, quantity, status
 *   2. A JpaRepository for it
 *   3. A @Service that creates and queries orders
 *   4. A @RestController with POST and GET endpoints
 *
 * These tests define the API contract. Make them pass.
 *
 * Tip: Use what you learned in koans 1-7!
 */
@SpringBootApplication(scanBasePackages = ["com.workshop.orders.miniproject"])
class MiniProjectTestApp

@SpringBootTest(classes = [MiniProjectTestApp::class])
@AutoConfigureMockMvc
class MiniProjectTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `POST api orders should create an order and return 201`() {
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"item": "Widget", "quantity": 3}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.item").value("Widget"))
            .andExpect(jsonPath("$.quantity").value(3))
            .andExpect(jsonPath("$.status").value("CREATED"))
    }

    @Test
    fun `GET api orders by status should return matching orders`() {
        // Create an order first
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"item": "Gadget", "quantity": 1}""")
        )

        mockMvc.perform(get("/api/orders").param("status", "CREATED"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].item").exists())
    }
}
