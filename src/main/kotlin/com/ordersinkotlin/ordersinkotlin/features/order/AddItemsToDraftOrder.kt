package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.domain.order.OrdersRepository
import com.ordersinkotlin.ordersinkotlin.domain.product.ProductsRepository
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.features.data.asOrderItem
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

class AddItemsToDraftOrder {
    data class Command(val orderId: String, val products: List<OrderProduct>)

    @Configuration
    class Endpoint {

        data class Request(val products: List<OrderProduct>)

        @Bean
        fun addItemsToDraftOrderRoute(handler: Handler) = coRouter {
            POST("/orders/{id}/items") {
                val request = it.awaitBody<Request>()
                val orderId = it.pathVariable("id")

                handler.handle(Command(orderId, request.products))

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
        private val repository: OrdersRepository,
        private val productsRepository: ProductsRepository
    ) : CommandHandler.WithoutResult<Command> {
        override suspend fun handle(command: Command) {
            val order = repository.findById(command.orderId)
                .orElseGet { throw IllegalArgumentException("Order does not exist") }

            val products = productsRepository
                .findAllById(command.products.map { it.productId })
                .map { it.id to it }
                .toMap()

            order.add(command.products
                .map {
                    val existingProduct = products.getOrElse(it.productId) {
                        throw IllegalArgumentException("Product ${it.productId} does not exist")
                    }

                    it.asOrderItem(existingProduct.price)
                }
            )

            uow.commitTo<OrdersRepository>(order)
        }
    }
}