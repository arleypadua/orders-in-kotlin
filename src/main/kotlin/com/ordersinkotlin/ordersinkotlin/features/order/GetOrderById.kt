package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.order.OrdersRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

class GetOrderById {
    @Configuration
    class Endpoint {
        @Bean
        fun getOrderByIdRoute(repository: OrdersRepository) = coRouter {
            GET("/orders/{id}") {
                val orderId = it.pathVariable("id")
                val order = repository.findById(orderId)

                ServerResponse
                    .ok()
                    .bodyValue(order)
                    .awaitSingle()
            }
        }
    }
}