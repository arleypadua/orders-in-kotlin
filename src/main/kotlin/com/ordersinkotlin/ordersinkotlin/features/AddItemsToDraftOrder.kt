package com.ordersinkotlin.ordersinkotlin.features

import com.ordersinkotlin.ordersinkotlin.domain.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.features.data.asOrderItem
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

class AddItemsToDraftOrder {
    data class Command(val orderId: String, val products: List<OrderProduct>)

    @Component
    class Handler(
        private val uow: UnitOfWork,
        private val repository: OrdersRepository
    ) : CommandHandler.WithoutResult<Command> {
        private val random = Random(0)

        override suspend fun handle(command: Command) {
            val order = repository.findById(command.orderId)
                .orElseGet { throw IllegalArgumentException("Order does not exist") }

            order.add(command.products
                // todo get current price from product
                .map { it.asOrderItem(random.nextDouble(1.0, 250.0)) }
            )

            uow.commitTo<OrdersRepository>(order)
        }
    }
}