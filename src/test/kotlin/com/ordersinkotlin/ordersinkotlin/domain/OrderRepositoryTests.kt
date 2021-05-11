package com.ordersinkotlin.ordersinkotlin.domain

import com.ordersinkotlin.ordersinkotlin.seedwork.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener
import java.util.*

@DataMongoTest
@Import(
    SpringApplicationEventPublisher::class,
    SpringUnitOfWork::class
)
class OrderRepositoryTests {

    @Autowired
    lateinit var repository: OrdersRepository

    @Autowired
    lateinit var uow: SpringUnitOfWork

    @Autowired
    lateinit var handler: Handler

    @BeforeEach
    fun before() {
        repository.deleteAll()
    }

    @Test
    fun `given order placed, when saving, should be saved and side-effects should be handled`() {
        val order = OrderFixture.getDraftOrder()
        order.add(
            OrderItem(
                Product(UUID.randomUUID()),
                30.0,
                1
            )
        )

        order.place()

        uow.commitTo<OrdersRepository>(order)

        val restoredOrder = repository.findById(order.id)
            .get()

        assertThat(restoredOrder.id).isEqualTo(order.id)
        assertThat(restoredOrder.customer.id).isEqualTo(order.customer.id)
        assertThat(restoredOrder.items.size).isEqualTo(order.items.size)

        assertThat(handler.handled).isTrue
    }

    @TestConfiguration
    class Handler {

        var handled = false

        @EventListener
        fun handle(event: OrderPlacedEvent) {
            handled = true
        }
    }
}