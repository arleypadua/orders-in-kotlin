package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.*
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter
import java.util.*

class CreateDraftOrder {
    data class Command(val customerId: UUID, val products: List<OrderProduct>)
    data class Result(val id: String)

    @Configuration
    class Endpoint {
        @Bean
        fun createDraftOrderRoute(handler: Handler) = coRouter {
            POST("/orders/draft") {
                val command = it.awaitBody<Command>()
                val result = handler.handle(command)

                ServerResponse
                    .ok()
                    .bodyValue(result)
                    .awaitSingle()
            }
        }
    }

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