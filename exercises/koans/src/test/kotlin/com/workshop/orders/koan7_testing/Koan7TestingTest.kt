package com.workshop.orders.koan7_testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Koan 7: Testing Slices
 *
 * Part A: Complete the @WebMvcTest for OrderController.
 * Part B: Complete the @DataJpaTest for OrderRepository.
 *
 * The annotations and injections are already in place — you write the test bodies.
 */

// Part A: Web layer test — controller only, services are mocked
@WebMvcTest(OrderController::class)
class Koan7WebMvcTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var orderService: OrderService

    @Test
    fun `GET orders should return list as JSON`() {
        // TODO: Stub orderService.findByStatus("CREATED") to return a list with one Order
        //       Then perform GET /orders?status=CREATED and assert:
        //       - status is 200
        //       - first element has the correct item name

        // Uncomment and complete:
        // `when`(orderService.findByStatus("CREATED"))
        //     .thenReturn(listOf(Order(item = "Widget", quantity = 2, id = 1)))
        //
        // mockMvc.perform(get("/orders").param("status", "CREATED"))
        //     .andExpect(status().isOk)
        //     .andExpect(jsonPath("$[0].item").value("Widget"))

        TODO("Complete this test")
    }
}

// Part B: Data layer test — repository with in-memory H2
@DataJpaTest
class Koan7DataJpaTest {

    @Autowired
    lateinit var repository: OrderRepository

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Test
    fun `should find orders by status`() {
        // TODO: Persist an Order using entityManager, then query by status.
        //
        // Uncomment and complete:
        // entityManager.persistAndFlush(Order(item = "Gadget", quantity = 1, status = "SHIPPED"))
        //
        // val results = repository.findByStatus("SHIPPED")
        // assertThat(results).hasSize(1)
        // assertThat(results.first().item).isEqualTo("Gadget")

        TODO("Complete this test")
    }
}
