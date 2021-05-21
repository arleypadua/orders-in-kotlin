package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.order.OrderStatus
import com.ordersinkotlin.ordersinkotlin.domain.order.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class CreateDraftOrderTests {

    @Autowired
    lateinit var handler: CommandHandler<CreateDraftOrder.Command, CreateDraftOrder.Result>

    @Autowired
    lateinit var repository: OrdersRepository

    @Test
    fun `given valid input, when creating draft order, should be created`() {
        val result = runBlocking {
            handler.handle(CreateDraftOrder.Command(UUID.randomUUID(), listOf()))
        }

        val created = repository.findById(result.id)
        assertThat(created.isPresent).isTrue
        assertThat(created.get().items.size).isEqualTo(0)
        assertThat(created.get().status).isEqualTo(OrderStatus.Draft)
    }
}