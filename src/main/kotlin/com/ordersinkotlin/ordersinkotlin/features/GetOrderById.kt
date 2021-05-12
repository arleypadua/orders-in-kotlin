package com.ordersinkotlin.ordersinkotlin.features

import com.ordersinkotlin.ordersinkotlin.domain.OrdersRepository
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