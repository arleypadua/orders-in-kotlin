package com.ordersinkotlin.ordersinkotlin.features

import com.ordersinkotlin.ordersinkotlin.domain.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import org.springframework.stereotype.Component
import java.util.*

class PlaceOrder {
    data class Command(val orderId: String)

    @Component
    class Handler(
        private val uow: UnitOfWork,
        private val repository: OrdersRepository
    ) : CommandHandler.WithoutResult<Command> {
        override suspend fun handle(command: Command) {
            val order = repository.findById(command.orderId)
                .orElseGet { throw IllegalArgumentException("Order does not exist") }

            order.place()

            uow.commitTo<OrdersRepository>(order)
        }
    }
}