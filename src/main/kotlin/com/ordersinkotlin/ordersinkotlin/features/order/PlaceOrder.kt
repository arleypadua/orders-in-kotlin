package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.order.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

class PlaceOrder {
    data class Command(val orderId: String)

    @Configuration
    class Endpoint {
        @Bean
        fun placeOrderRoute(handler: Handler) = coRouter {
            POST("/orders/{id}/place") {
                val orderId = it.pathVariable("id")
                handler.handle(Command(orderId))

                ServerResponse
                    .ok()
                    .bodyValue(object {})
                    .awaitSingle()
            }
        }
    }

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