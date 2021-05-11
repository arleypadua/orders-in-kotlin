package com.ordersinkotlin.ordersinkotlin.features

import com.ordersinkotlin.ordersinkotlin.domain.*
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import org.springframework.stereotype.Component
import java.util.*

class CreateDraftOrder {
    data class Command(val customerId: UUID, val products: List<OrderProduct>)
    data class Result(val id: String)

    @Component
    class Handler(
        private val uow: UnitOfWork
    ) : CommandHandler<Command, Result> {
        override suspend fun handle(command: Command): Result {
            val order = Order.draft(
                Customer(command.customerId) // todo validate whether customer exists
            )

            order.add(
                command.products
                    .map {
                        OrderItem(Product(it.productId), 50.0, it.quantity)
                    }) // todo get current product prices

            uow.commitTo<OrdersRepository>(order)

            return Result(order.id)
        }
    }
}