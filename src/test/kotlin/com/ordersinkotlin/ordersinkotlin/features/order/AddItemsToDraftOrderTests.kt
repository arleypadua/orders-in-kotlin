package com.ordersinkotlin.ordersinkotlin.features

import com.ordersinkotlin.ordersinkotlin.domain.Order
import com.ordersinkotlin.ordersinkotlin.domain.OrderFixture
import com.ordersinkotlin.ordersinkotlin.domain.OrderStatus
import com.ordersinkotlin.ordersinkotlin.domain.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class AddItemsToDraftOrderTests {

    @Autowired
    lateinit var handler: CommandHandler.WithoutResult<AddItemsToDraftOrder.Command>

    @Autowired
    lateinit var repository: OrdersRepository

    @Test
    fun `given existing order, when adding items, items should be added`() {
        val order = createOrder()
        val productToAdd = OrderProduct(UUID.randomUUID(), 2)
        val command = AddItemsToDraftOrder.Command(order.id, listOf(productToAdd))

        runBlocking {
            handler.handle(command)
        }

        val orderFromStorage = repository.findById(order.id)
            .get()

        assertThat(orderFromStorage.items).anyMatch { it.product.id == productToAdd.productId }
    }

    private fun createOrder(): Order {
        val order = OrderFixture.getDraftOrder(true)
        repository.save(order)

        return order
    }
}