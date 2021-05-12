package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.Order
import com.ordersinkotlin.ordersinkotlin.domain.OrderFixture
import com.ordersinkotlin.ordersinkotlin.domain.OrderStatus
import com.ordersinkotlin.ordersinkotlin.domain.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PlaceOrderTests {

    @Autowired
    lateinit var handler: CommandHandler.WithoutResult<PlaceOrder.Command>

    @Autowired
    lateinit var repository: OrdersRepository

    @Test
    fun `given existing order, when placing, should be placed`() {
        val order = createOrder()

        val result = runBlocking {
            handler.handle(PlaceOrder.Command(order.id))
        }

        val placed = repository.findById(order.id)
            .get()

        assertThat(placed.status).isEqualTo(OrderStatus.Placed)
    }

    private fun createOrder(): Order {
        val order = OrderFixture.getDraftOrder(true)
        repository.save(order)

        return order
    }
}