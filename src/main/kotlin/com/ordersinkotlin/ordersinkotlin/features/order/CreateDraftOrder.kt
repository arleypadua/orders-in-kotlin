package com.ordersinkotlin.ordersinkotlin.features.order

import com.ordersinkotlin.ordersinkotlin.crosscutting.logger
import com.ordersinkotlin.ordersinkotlin.crosscutting.structuredInfo
import com.ordersinkotlin.ordersinkotlin.domain.order.*
import com.ordersinkotlin.ordersinkotlin.domain.product.ProductsRepository
import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import kotlinx.coroutines.reactive.awaitSingle
import net.logstash.logback.argument.StructuredArguments.keyValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter
import java.util.*

class CreateDraftOrder {
    data class Command(val customerId: String, val products: List<OrderProduct>)
    data class Result(val id: String)

    @Configuration
    class Endpoint {

        private val logger = logger()

        @Bean
        fun createDraftOrderRoute(handler: Handler) = coRouter {
            POST("/orders/draft") {
                val command = it.awaitBody<Command>()
                val result = handler.handle(command)

                logger.structuredInfo(
                    "Created order {orderId} with {quantityOfProducts} products",
                    result.id,
                    command.products.size,
                    keyValue("command", command)
                )

                ServerResponse
                    .ok()
                    .bodyValue(result)
                    .awaitSingle()
            }
        }
    }

    @Component
    class Handler(
        private val uow: UnitOfWork,
        private val productsRepository: ProductsRepository
    ) : CommandHandler<Command, Result> {
        override suspend fun handle(command: Command): Result {
            val order = Order.draft(
                Customer(command.customerId) // todo validate whether customer exists
            )

            if(command.products.any()) {
                val products = productsRepository
                    .findAllById(command.products.map { it.productId })
                    .map { it.id to it }
                    .toMap()

                order.add(
                    command.products
                        .map {
                            val existingProduct = products.getOrElse(it.productId) {
                                throw IllegalArgumentException("Product ${it.productId} does not exist")
                            }

                            OrderItem(Product(it.productId), existingProduct.price, it.quantity)
                        })
            }

            uow.commitTo<OrdersRepository>(order)

            return Result(order.id)
        }
    }
}