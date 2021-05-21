package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.order.*
import com.ordersinkotlin.ordersinkotlin.domain.product.ProductsRepository
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddItemsToDraftOrderTests {

    @Autowired
    lateinit var handler: CommandHandler.WithoutResult<AddItemsToDraftOrder.Command>

    @Autowired
    lateinit var repository: OrdersRepository

    @Autowired
    lateinit var productRepository: ProductsRepository

    val product = com.ordersinkotlin.ordersinkotlin.domain.product.Product.new(50.0)

    @BeforeAll
    fun setup() {
        productRepository.save(product)
    }

    @Test
    fun `given existing order, when adding items, items should be added`() {
        val order = createOrder()
        val productToAdd = OrderProduct(product.id, 2)
        val command = AddItemsToDraftOrder.Command(order.id, listOf(productToAdd))

        runBlocking {
            handler.handle(command)
        }

        val orderFromStorage = repository.findById(order.id)
            .get()

        assertThat(orderFromStorage.items).anyMatch { it.product.id == productToAdd.productId }
    }

    @Test
    fun `given existing order, when adding item that doesn't exist, error should be thrown`() {
        val order = createOrder()
        val productToAdd = OrderProduct("random-id", 2)
        val command = AddItemsToDraftOrder.Command(order.id, listOf(productToAdd))

        runBlocking {
            assertThrows<IllegalArgumentException> {
                handler.handle(command)
            }
        }
    }

    private fun createOrder(): Order {
        val order = OrderFixture.getDraftOrder(OrderItem(
            Product(product.id),
            product.price,
            1
        ))
        repository.save(order)

        return order
    }
}